# 토대가 되는 이미지 선택. 자바스크립트 런타임 환경 선택.
FROM node:20

# 순수 런타임 이미지로 만든 컨테이너에서 기본 디렉토리 구조는 app이라는 폴더가 존재하지 않으므로, 작업환경으로 사용할 app이라는 디렉토리를 하나 만들어주자.
RUN mkdir -p /app

# 작업 디렉토리로서 app 폴더를 선택하기.
WORKDIR /app

# 프로젝트 파일 전부를 작업 디렉토리(app 폴더)로 복사하기.
COPY . .

# 필요없는 폴더 및 파일들을 이미지에서 삭제하기.
RUN rm -rf node_modules

# 이미지 내부에 존재하는 package.json 파일과 package-lock.json 파일을 바탕으로 필요한 라이브러리를 이미지에 추가.
RUN npm install

# serve package 따로 설치
RUN npm install --global serve

# dist 폴더(빌드 결과물)를 이미지에 추가.
RUN npm run build

# 컨테이너를 실행할 때 실행할 명령어를 강제 지정.
ENTRYPOINT ["npx", "serve", "-s", "dist", "-l", "5173"]

# 컨테이너 노출 포트 지정.
EXPOSE 5173