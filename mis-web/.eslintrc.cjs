/* eslint-env node */
require('@rushstack/eslint-patch/modern-module-resolution');

module.exports = {
  root: true,
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    'airbnb-base',
    '@vue/eslint-config-prettier/skip-formatting',
    'plugin:prettier/recommended',
    './.eslintrc-auto-import.json',
  ],
  parserOptions: {
    ecmaVersion: 'latest',
  },
  settings: {
    'import/resolver': {
      alias: {
        map: [['@', './src']],
        extensions: ['.js', '.vue'],
      },
    },
    'import/core-modules': [
      'vite',
      '@vitejs/plugin-vue',
      'dompurify',
      'pinia-plugin-persistedstate',
    ],
  },
  rules: {
    'no-console': 'off',
    'no-unused-vars': 'off',
    'import/prefer-default-export': 'off',
    'import/no-extraneous-dependencies': ['error', { devDependencies: true }],
    'no-restricted-globals': ['off'],
    'no-use-before-define': ['off'],
    // 允許使用==
    eqeqeq: 'off',
    'vue/no-v-html': 'off',
    'no-plusplus': 'off',
  },
};
