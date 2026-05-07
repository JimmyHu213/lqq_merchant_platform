export default {
  extends: ["@commitlint/config-conventional"],
  ignores: [(message) => /^Bump .+ from .+ to .+$/m.test(message)],
};
