package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.common.model.promoter.PromoterMerchant;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.wxjava.WxPayServiceFactory;
import com.zbkj.service.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 分账核心业务测试 — 覆盖 Phase 5 集成测试 5.2/5.3/5.4
 * 使用 Mockito Mock 所有外部依赖，验证分账计算逻辑正确性
 */
@ExtendWith(MockitoExtension.class)
public class LqqProfitSharingServiceTest {

    @InjectMocks
    private LqqProfitSharingServiceImpl profitSharingService;

    @Mock
    private RedisUtil redisUtil;
    @Mock
    private MerchantService merchantService;
    @Mock
    private OrderService orderService;
    @Mock
    private MerchantOrderService merchantOrderService;
    @Mock
    private UserService userService;
    @Mock
    private UserTokenService userTokenService;
    @Mock
    private WechatProfitSharingRecordService recordService;
    @Mock
    private WxPayServiceFactory wxPayServiceFactory;
    @Mock
    private SystemConfigService systemConfigService;
    @Mock
    private PromoterMerchantService promoterMerchantService;

    @Captor
    private ArgumentCaptor<List<WechatProfitSharingRecord>> recordsCaptor;

    // 测试常量
    private static final Integer BUYER_UID = 70001;
    private static final Integer REFERRER_UID = 60001;  // 推荐人
    private static final Integer AGENT_UID = 50001;     // 代理人
    private static final Integer CONSUMING_MER_ID = 100; // 消费商户
    private static final Integer LOCKED_MER_ID = 200;    // 锁客商户
    private static final String ORDER_NO = "TEST202605090001";
    private static final String OUT_TRADE_NO = "wx_txn_mock_001";

    private Merchant consumingMerchant;
    private MerchantOrder merchantOrder;
    private Order platOrder;
    private User buyer;

    @BeforeEach
    void setUp() {
        // 消费商户: fszk=10% (让利比例), isProfitSharing=true
        consumingMerchant = new Merchant();
        consumingMerchant.setId(CONSUMING_MER_ID);
        consumingMerchant.setName("测试消费商户");
        consumingMerchant.setIsProfitSharing(true);
        consumingMerchant.setFszk(new BigDecimal("10")); // 10% 让利
        consumingMerchant.setAdminId(60100);

        // 商户订单: 实付100元
        merchantOrder = new MerchantOrder();
        merchantOrder.setOrderNo(ORDER_NO);
        merchantOrder.setMerId(CONSUMING_MER_ID);
        merchantOrder.setUid(BUYER_UID);
        merchantOrder.setPayPrice(new BigDecimal("100.00"));

        // 平台订单
        platOrder = new Order();
        platOrder.setOrderNo(ORDER_NO);
        platOrder.setOutTradeNo(OUT_TRADE_NO);

        // 消费者: 无锁客, 无推荐人（默认）
        buyer = new User();
        buyer.setId(BUYER_UID);
        buyer.setNickname("测试买家");
        buyer.setSpreadUid(0);
        buyer.setLockedMerchantId(0);
    }

    /** 通用 Mock 设置 — 部分 stub 可能不被所有测试调用，使用 lenient */
    private void setupBasicMocks() {
        when(recordService.getByOrderNo(ORDER_NO)).thenReturn(new ArrayList<>());
        when(merchantService.getByIdException(CONSUMING_MER_ID)).thenReturn(consumingMerchant);
        when(orderService.getByOrderNo(ORDER_NO)).thenReturn(platOrder);
        lenient().when(userService.getById(BUYER_UID)).thenReturn(buyer);
        // 默认: 非首单 (orderCount > 1)
        lenient().when(merchantOrderService.count(any(LambdaQueryWrapper.class))).thenReturn(2);
        // 默认: 无代理
        lenient().when(promoterMerchantService.getByMerId(anyInt())).thenReturn(null);
        // 默认: 首单系数1.5, 裂变比例5%, 冻结3天
        lenient().when(systemConfigService.getValueByKey("lqq_first_order_coefficient")).thenReturn("1.5");
        lenient().when(systemConfigService.getValueByKey("lqq_referral_commission_rate")).thenReturn("5");
        lenient().when(systemConfigService.getValueByKey("lqq_commission_freeze_days")).thenReturn("3");
    }

    // ========================================================================
    // 5.2 支付→分账全链路
    // ========================================================================
    @Nested
    @DisplayName("5.2 支付→分账全链路")
    class PaymentToProfitSharingTest {

        @Test
        @DisplayName("5.2.1 基础分账 — 无推荐人/代理/锁客，全部归平台")
        void testBasicProfitSharing_allToPlatform() {
            setupBasicMocks();

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 100 × 10% = 10.00
            // 无推荐人/代理/锁客 → 平台拿全部 10.00
            assertEquals(1, records.size());
            WechatProfitSharingRecord platform = records.get(0);
            assertEquals("PLATFORM", platform.getReceiverType());
            assertEquals(new BigDecimal("10.00"), platform.getAmount());
        }

        @Test
        @DisplayName("5.2.2 首单翻倍 — 用户首次在该商户消费，分账池×1.5")
        void testFirstOrderBonus() {
            setupBasicMocks();
            // 首单: orderCount = 1
            when(merchantOrderService.count(any(LambdaQueryWrapper.class))).thenReturn(1);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 100 × 10% × 1.5 = 15.00
            BigDecimal totalAmount = records.stream()
                    .map(WechatProfitSharingRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertEquals(0, new BigDecimal("15.00").compareTo(totalAmount),
                    "首单翻倍后分账池应为15.00，实际: " + totalAmount);
        }

        @Test
        @DisplayName("5.2.3 裂变佣金 — 推荐人拿分账池的5%")
        void testReferralCommission() {
            setupBasicMocks();
            // 设置推荐人
            buyer.setSpreadUid(REFERRER_UID);
            User referrer = new User();
            referrer.setId(REFERRER_UID);
            referrer.setNickname("推荐人");
            when(userService.getById(REFERRER_UID)).thenReturn(referrer);

            UserToken token = new UserToken();
            token.setToken("mock_openid_referrer");
            lenient().when(userTokenService.getTokenByUserId(eq(REFERRER_UID), anyInt())).thenReturn(token);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 10.00
            // 裂变佣金 = 10.00 × 5% = 0.50
            WechatProfitSharingRecord referralRecord = records.stream()
                    .filter(r -> "REFERRER".equals(r.getReceiverType()))
                    .findFirst().orElse(null);
            assertNotNull(referralRecord, "应有裂变佣金记录");
            assertEquals(0, new BigDecimal("0.50").compareTo(referralRecord.getAmount()),
                    "裂变佣金应为0.50");
            assertEquals(REFERRER_UID, referralRecord.getReceiverUserId());

            // 平台 = 10.00 - 0.50 = 9.50
            WechatProfitSharingRecord platformRecord = records.stream()
                    .filter(r -> "PLATFORM".equals(r.getReceiverType()))
                    .findFirst().orElse(null);
            assertNotNull(platformRecord);
            assertEquals(0, new BigDecimal("9.50").compareTo(platformRecord.getAmount()));
        }

        @Test
        @DisplayName("5.2.4 幂等保护 — 已有分账记录时跳过")
        void testIdempotent_skipDuplicate() {
            // 模拟已存在记录
            List<WechatProfitSharingRecord> existing = new ArrayList<>();
            existing.add(new WechatProfitSharingRecord());
            when(recordService.getByOrderNo(ORDER_NO)).thenReturn(existing);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            // 不应保存新记录
            verify(recordService, never()).saveBatch(any());
        }

        @Test
        @DisplayName("5.2.5 未启用分账 — 商户 isProfitSharing=false 时跳过")
        void testSkipWhenNotEnabled() {
            when(recordService.getByOrderNo(ORDER_NO)).thenReturn(new ArrayList<>());
            consumingMerchant.setIsProfitSharing(false);
            when(merchantService.getByIdException(CONSUMING_MER_ID)).thenReturn(consumingMerchant);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService, never()).saveBatch(any());
        }

        @Test
        @DisplayName("5.2.6 分账池不足0.01时跳过")
        void testSkipWhenPoolTooSmall() {
            setupBasicMocks();
            // 实付0.05, 分账池 = 0.05 × 10% = 0.005 < 0.01
            merchantOrder.setPayPrice(new BigDecimal("0.05"));

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService, never()).saveBatch(any());
        }
    }

    // ========================================================================
    // 5.3 锁客→分润全链路
    // ========================================================================
    @Nested
    @DisplayName("5.3 锁客→分润全链路")
    class LockCustomerProfitTest {

        @Test
        @DisplayName("5.3.1 跨店消费 — 锁客商户拿 skzk% 分润")
        void testCrossStoreProfitSharing() {
            setupBasicMocks();
            // 买家锁定在商户200, 消费在商户100 (跨店)
            buyer.setLockedMerchantId(LOCKED_MER_ID);

            Merchant lockedMerchant = new Merchant();
            lockedMerchant.setId(LOCKED_MER_ID);
            lockedMerchant.setName("锁客商户");
            lockedMerchant.setSkzk(new BigDecimal("8")); // 8% 锁客比例
            lockedMerchant.setAdminId(60200);
            when(merchantService.getByIdException(LOCKED_MER_ID)).thenReturn(lockedMerchant);
            lenient().when(userTokenService.getTokenByUserId(eq(60200), anyInt())).thenReturn(null);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 100 × 10% = 10.00
            // 锁客分润 = 10.00 × 8% = 0.80
            WechatProfitSharingRecord lockRecord = records.stream()
                    .filter(r -> "LOCKED_MERCHANT".equals(r.getReceiverType()))
                    .findFirst().orElse(null);
            assertNotNull(lockRecord, "跨店消费应有锁客分润记录");
            assertEquals(0, new BigDecimal("0.80").compareTo(lockRecord.getAmount()),
                    "锁客分润应为0.80");
            assertEquals("锁客商户", lockRecord.getReceiverName());

            // 平台 = 10.00 - 0.80 = 9.20
            WechatProfitSharingRecord platformRecord = records.stream()
                    .filter(r -> "PLATFORM".equals(r.getReceiverType()))
                    .findFirst().orElse(null);
            assertNotNull(platformRecord);
            assertEquals(0, new BigDecimal("9.20").compareTo(platformRecord.getAmount()));
        }

        @Test
        @DisplayName("5.3.2 本店消费 — 消费店=锁客店，锁客分润=0")
        void testSameStoreProfitSharing_noLockProfit() {
            setupBasicMocks();
            // 买家锁定在商户100, 消费也在商户100 (同店)
            buyer.setLockedMerchantId(CONSUMING_MER_ID);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 不应有锁客分润记录
            boolean hasLockRecord = records.stream()
                    .anyMatch(r -> "LOCKED_MERCHANT".equals(r.getReceiverType()));
            assertFalse(hasLockRecord, "同店消费不应有锁客分润记录");

            // 全部归平台
            assertEquals(1, records.size());
            assertEquals("PLATFORM", records.get(0).getReceiverType());
        }

        @Test
        @DisplayName("5.3.3 跨店+裂变 — 锁客分润和裂变佣金同时存在")
        void testCrossStore_withReferral() {
            setupBasicMocks();
            buyer.setLockedMerchantId(LOCKED_MER_ID);
            buyer.setSpreadUid(REFERRER_UID);

            // 锁客商户
            Merchant lockedMerchant = new Merchant();
            lockedMerchant.setId(LOCKED_MER_ID);
            lockedMerchant.setName("锁客商户");
            lockedMerchant.setSkzk(new BigDecimal("8"));
            lockedMerchant.setAdminId(60200);
            when(merchantService.getByIdException(LOCKED_MER_ID)).thenReturn(lockedMerchant);
            lenient().when(userTokenService.getTokenByUserId(eq(60200), anyInt())).thenReturn(null);

            // 推荐人
            User referrer = new User();
            referrer.setId(REFERRER_UID);
            referrer.setNickname("推荐人");
            when(userService.getById(REFERRER_UID)).thenReturn(referrer);
            lenient().when(userTokenService.getTokenByUserId(eq(REFERRER_UID), anyInt())).thenReturn(null);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 10.00
            // 裂变 = 10.00 × 5% = 0.50
            // 锁客 = 10.00 × 8% = 0.80
            // 平台 = 10.00 - 0.50 - 0.80 = 8.70
            assertEquals(3, records.size(), "应有3条记录: 裂变+锁客+平台");

            BigDecimal total = records.stream()
                    .map(WechatProfitSharingRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertEquals(0, new BigDecimal("10.00").compareTo(total),
                    "各方分润总和应等于分账池10.00");
        }
    }

    // ========================================================================
    // 5.4 推广员→佣金全链路
    // ========================================================================
    @Nested
    @DisplayName("5.4 推广员→佣金全链路")
    class PromoterCommissionTest {

        @Test
        @DisplayName("5.4.1 代理佣金 — 消费者是代理人的粉丝，代理拿 commissionRate%")
        void testAgentCommission_fanBuys() {
            setupBasicMocks();
            // 买家的 spreadUid = 代理人 (直接粉丝)
            buyer.setSpreadUid(AGENT_UID);

            // 代理绑定
            PromoterMerchant agentBind = new PromoterMerchant();
            agentBind.setUid(AGENT_UID);
            agentBind.setMerId(CONSUMING_MER_ID);
            agentBind.setCommissionRate(new BigDecimal("10")); // 10% 代理佣金
            when(promoterMerchantService.getByMerId(CONSUMING_MER_ID)).thenReturn(agentBind);

            // 代理人用户
            User agent = new User();
            agent.setId(AGENT_UID);
            agent.setNickname("代理人");
            agent.setSpreadUid(0);
            when(userService.getById(AGENT_UID)).thenReturn(agent);
            lenient().when(userTokenService.getTokenByUserId(eq(AGENT_UID), anyInt())).thenReturn(null);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 10.00
            // 裂变佣金(spreadUid=AGENT): 10.00 × 5% = 0.50
            // 代理佣金: 10.00 × 10% = 1.00
            // 平台: 10.00 - 0.50 - 1.00 = 8.50
            WechatProfitSharingRecord agentRecord = records.stream()
                    .filter(r -> "AGENT".equals(r.getReceiverType()))
                    .findFirst().orElse(null);
            assertNotNull(agentRecord, "应有代理佣金记录");
            assertEquals(0, new BigDecimal("1.00").compareTo(agentRecord.getAmount()),
                    "代理佣金应为1.00");
            assertEquals(AGENT_UID, agentRecord.getReceiverUserId());
        }

        @Test
        @DisplayName("5.4.2 非粉丝不分佣 — 消费者不在代理人的 spreadUid 链上")
        void testAgentCommission_notFan() {
            setupBasicMocks();
            // 买家的 spreadUid 不指向代理人
            buyer.setSpreadUid(99999); // 其他人

            User otherUser = new User();
            otherUser.setId(99999);
            otherUser.setNickname("其他推荐人");
            otherUser.setSpreadUid(0); // 链断了，不包含代理人
            when(userService.getById(99999)).thenReturn(otherUser);
            lenient().when(userTokenService.getTokenByUserId(eq(99999), anyInt())).thenReturn(null);

            // 代理绑定存在
            PromoterMerchant agentBind = new PromoterMerchant();
            agentBind.setUid(AGENT_UID);
            agentBind.setMerId(CONSUMING_MER_ID);
            agentBind.setCommissionRate(new BigDecimal("10"));
            when(promoterMerchantService.getByMerId(CONSUMING_MER_ID)).thenReturn(agentBind);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 不应有代理佣金
            boolean hasAgentRecord = records.stream()
                    .anyMatch(r -> "AGENT".equals(r.getReceiverType()));
            assertFalse(hasAgentRecord, "非粉丝消费不应有代理佣金");
        }

        @Test
        @DisplayName("5.4.3 间接粉丝 — 2层 spreadUid 链上有代理人")
        void testAgentCommission_indirectFan() {
            setupBasicMocks();
            // 买家 → 中间人 → 代理人 (2层粉丝链)
            Integer middleUid = 65000;
            buyer.setSpreadUid(middleUid);

            User middleUser = new User();
            middleUser.setId(middleUid);
            middleUser.setNickname("中间人");
            middleUser.setSpreadUid(AGENT_UID);
            when(userService.getById(middleUid)).thenReturn(middleUser);
            lenient().when(userTokenService.getTokenByUserId(eq(middleUid), anyInt())).thenReturn(null);

            User agent = new User();
            agent.setId(AGENT_UID);
            agent.setNickname("代理人");
            agent.setSpreadUid(0);
            when(userService.getById(AGENT_UID)).thenReturn(agent);
            lenient().when(userTokenService.getTokenByUserId(eq(AGENT_UID), anyInt())).thenReturn(null);

            // 代理绑定
            PromoterMerchant agentBind = new PromoterMerchant();
            agentBind.setUid(AGENT_UID);
            agentBind.setMerId(CONSUMING_MER_ID);
            agentBind.setCommissionRate(new BigDecimal("8")); // 8%
            when(promoterMerchantService.getByMerId(CONSUMING_MER_ID)).thenReturn(agentBind);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 10.00
            // 裂变(spreadUid=middleUid): 10.00 × 5% = 0.50
            // 代理(间接粉丝): 10.00 × 8% = 0.80
            // 平台: 10.00 - 0.50 - 0.80 = 8.70
            WechatProfitSharingRecord agentRecord = records.stream()
                    .filter(r -> "AGENT".equals(r.getReceiverType()))
                    .findFirst().orElse(null);
            assertNotNull(agentRecord, "间接粉丝消费应有代理佣金");
            assertEquals(0, new BigDecimal("0.80").compareTo(agentRecord.getAmount()));
        }

        @Test
        @DisplayName("5.4.4 四方全分 — 裂变+代理+锁客+平台 全部参与")
        void testFullFourWaySplit() {
            setupBasicMocks();
            // 买家有推荐人(也是代理的粉丝链上), 锁定在另一家店
            buyer.setSpreadUid(AGENT_UID); // 直接粉丝=代理人本人
            buyer.setLockedMerchantId(LOCKED_MER_ID);

            // 代理绑定
            PromoterMerchant agentBind = new PromoterMerchant();
            agentBind.setUid(AGENT_UID);
            agentBind.setMerId(CONSUMING_MER_ID);
            agentBind.setCommissionRate(new BigDecimal("10")); // 10%
            when(promoterMerchantService.getByMerId(CONSUMING_MER_ID)).thenReturn(agentBind);

            // 代理人
            User agent = new User();
            agent.setId(AGENT_UID);
            agent.setNickname("代理人");
            agent.setSpreadUid(0);
            when(userService.getById(AGENT_UID)).thenReturn(agent);
            lenient().when(userTokenService.getTokenByUserId(eq(AGENT_UID), anyInt())).thenReturn(null);

            // 锁客商户
            Merchant lockedMerchant = new Merchant();
            lockedMerchant.setId(LOCKED_MER_ID);
            lockedMerchant.setName("锁客商户");
            lockedMerchant.setSkzk(new BigDecimal("8")); // 8%
            lockedMerchant.setAdminId(60200);
            when(merchantService.getByIdException(LOCKED_MER_ID)).thenReturn(lockedMerchant);
            lenient().when(userTokenService.getTokenByUserId(eq(60200), anyInt())).thenReturn(null);

            profitSharingService.createProfitSharingRecords(merchantOrder);

            verify(recordService).saveBatch(recordsCaptor.capture());
            List<WechatProfitSharingRecord> records = recordsCaptor.getValue();

            // 分账池 = 100 × 10% = 10.00
            // 裂变 = 10.00 × 5% = 0.50
            // 代理 = 10.00 × 10% = 1.00
            // 锁客 = 10.00 × 8% = 0.80
            // 平台 = 10.00 - 0.50 - 1.00 - 0.80 = 7.70
            assertEquals(4, records.size(), "四方分配应有4条记录");

            BigDecimal referralAmt = getAmountByType(records, "REFERRER");
            BigDecimal agentAmt = getAmountByType(records, "AGENT");
            BigDecimal lockAmt = getAmountByType(records, "LOCKED_MERCHANT");
            BigDecimal platformAmt = getAmountByType(records, "PLATFORM");

            assertEquals(0, new BigDecimal("0.50").compareTo(referralAmt), "裂变: 0.50");
            assertEquals(0, new BigDecimal("1.00").compareTo(agentAmt), "代理: 1.00");
            assertEquals(0, new BigDecimal("0.80").compareTo(lockAmt), "锁客: 0.80");
            assertEquals(0, new BigDecimal("7.70").compareTo(platformAmt), "平台: 7.70");

            // 总和校验
            BigDecimal total = referralAmt.add(agentAmt).add(lockAmt).add(platformAmt);
            assertEquals(0, new BigDecimal("10.00").compareTo(total),
                    "四方总和应等于分账池10.00");
        }
    }

    // ========================================================================
    // 工具方法
    // ========================================================================
    private BigDecimal getAmountByType(List<WechatProfitSharingRecord> records, String type) {
        return records.stream()
                .filter(r -> type.equals(r.getReceiverType()))
                .map(WechatProfitSharingRecord::getAmount)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
}
