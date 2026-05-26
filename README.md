# JAVA 콘솔 채팅 서버 - 클라이언트

## 프로젝트 소개

Java Socket과 TCP/IP 통신을 활용하여
콘솔 환경에서 동작하는 서버-클라이언트 기반 멀티룸 채팅 프로그램을 구현한 프로젝트입니다.

여러 사용자가 동시에 서버에 접속할 수 있으며,
닉네임 로그인, 채팅방 생성 및 이동, 귓속말, 접속자 목록 조회 등의 기능을 사용할 수 있습니다.

또한 Thread Pool과 ConcurrentHashMap을 적용하여
동시 접속 환경에서도 안정적으로 동작하도록 구현하였습니다.

---

# 개발 환경

| 항목 | 내용 |
| --- | --- |
| Language | Java 25 |
| IDE | IntelliJ IDEA |
| Protocol | TCP/IP |
| Network | Socket Programming |
| Concurrency | Thread Pool |
| Collection | ConcurrentHashMap |

---

# 핵심 기능

| 기능 | 설명 |
| --- | --- |
| TCP Socket 서버 구현 | 클라이언트와 서버 간 실시간 통신 |
| 다중 클라이언트 동시 접속 | 여러 사용자의 동시 접속 지원 |
| 로그인 기능 | 닉네임 기반 사용자 로그인 |
| 멀티룸 채팅 | 채팅방 생성 및 입장 가능 |
| 채팅방 이동 | `/join 방이름` 명령어 지원 |
| 귓속말 기능 | `/w 닉네임 메시지` 지원 |
| 채팅방 목록 조회 | `/rooms` 명령어 지원 |
| 접속자 목록 조회 | `/users` 명령어 지원 |
| 명령어 안내 | `/help` 명령어 지원 |
| 접속 로그 저장 | server_log.txt 자동 저장 |
| 안전한 자원 해제 | socket 및 stream close 처리 |
| Thread Pool 적용 | ExecutorService 사용 |
| ConcurrentHashMap 적용 | 동시성 환경 안정성 향상 |

---

# 실행 방법

## 1. 서버 실행

ChatServer.java 실행

```bash
java ChatServer
```

실행 시:

```
멀티룸 채팅 서버 시작
포트 번호: 5000
```

출력됩니다.

---

## 2. 클라이언트 실행

ChatClient.java 실행

```bash
java ChatClient
```

여러 사용자를 테스트하기 위해
IntelliJ의 다중 인스턴스 실행 기능을 사용하여
여러 개의 ChatClient를 동시에 실행할 수 있습니다.

---

# 명령어 사용 방법

## 채팅방 목록 조회

```
/rooms
```

---

## 현재 방 접속자 목록 조회

```
/users
```

---

## 다른 채팅방 이동

```
/join game
```

---

## 귓속말 보내기

```
/w 유자 안녕하세요
```

---

## 명령어 도움말

```
/help
```

---

## 채팅 종료

```
/quit
```

---

# 실행 화면

images 파일에 있음 

# 클래스 설명

| 클래스 | 역할 |
| --- | --- |
| ChatServer | 서버 실행 및 세션/채팅방 관리 |
| ChatClient | 클라이언트 실행 |
| ClientHandler | 클라이언트별 요청 처리 |
| ChatRoom | 채팅방 및 사용자 관리 |

---

# 구현 내용

- Java Socket 기반 TCP/IP 통신 구현
- Multi Thread 기반 동시 사용자 처리
- ExecutorService를 활용한 Thread Pool 적용
- ConcurrentHashMap 기반 세션 관리
- 채팅방 생성 및 이동 기능 구현
- 귓속말(Whisper) 기능 구현
- 접속 로그 파일 저장 기능 구현
- 안전한 자원 해제 처리 구현
- 명령어 기반 채팅 시스템 구현

---

# 로그 파일

서버 실행 시 프로젝트 폴더 내부에:

```
server_log.txt
```

파일이 생성되며,
사용자 접속/퇴장/채팅 로그가 자동 저장됩니다.
