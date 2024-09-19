module.exports = {
  root: true,
  parser: '@typescript-eslint/parser', // TypeScript 파서로 변경
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module',
    project: ['./tsconfig.json'], // TypeScript 프로젝트의 tsconfig.json 파일 경로를 명시
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended', // TypeScript 권장 규칙 추가
    'plugin:svelte3/recommended',
    'plugin:prettier/recommended',
  ],
  plugins: ['svelte3', '@typescript-eslint', 'prettier'], // TypeScript 플러그인 추가
  overrides: [
    {
      files: ['*.svelte'],
      processor: 'svelte3/svelte3',
    },
  ],
  settings: {
    'svelte3/typescript': true, // Svelte에서 TypeScript 지원 활성화
    'svelte3/ignore-styles': () => true, // SCSS와 같은 스타일 무시 설정
  },
  rules: {
    'prettier/prettier': 'error', // Prettier 규칙 적용
    '@typescript-eslint/no-unused-vars': ['error'], // TypeScript에서 사용하지 않는 변수 경고
    '@typescript-eslint/explicit-function-return-type': 'off', // 함수 반환 타입 명시 비활성화
  },
};
