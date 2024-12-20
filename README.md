## 프로젝트 설명
음원 스트리밍 및 가사 제공, 음원 순위, 아티스트 조회, 검색 등을 제공하는 서비스입니다.

## 프로젝트 기능 및 설계
- 회원가입 기능
  - 사용자는 일반 회원과 관리자 회원으로 나뉘어 진행할 수 있다.
  - 아이디는 unique 해야 하며, 패스워드는 암호화하여 저장된다.
 
- 로그인 기능
  - 회원가입을 완료한 사용자는 로그인할 수 있다. 회원가입 때 사용한 아이디와 패스워드가 일치해야 한다.

- 음원 스트리밍 기능
  - 일반 회원만 음원 스트리밍 기능을 사용할 수 있다.
  - 음원 상태가 'RELEASED'인 음원만 스트리밍할 수 있다.
  - 하나의 음원 스트리밍이 종료될 때마다 진행한 스트리밍의 정보를 저장한다.
  - 사용자의 네트워크 환경에 따라 품질을 조정하여 스트리밍을 제공한다.
  - 스트리밍이 종료되면 해당 스트리밍에 대한 정보 로그를 남긴다.
  - 로그 정보: 사용자 ID, 음원 ID, 재생 시작 시간, 재생 종료 시간, 음원 전체 길이(초) 실제 재생 시간(초)
 
- 가사 제공 기능
  - 음원 스트리밍을 진행하면 해당 음원의 가사가 제공된다.
  - 가사는 LRC/TEXT 형태이다.
  - LRC는 현재 재생 중인 가사의 인덱스를 클라이언트에 제공하고, 가사를 클릭하면 해당 가사의 부분이 재생된다.
 
- 음원 순위 조회 기능
  - 일반 회원은 음원 순위 조회가 가능하다.
  - 1위부터 100위까지 조회가 가능하다.
  - 10페이지씩 페이징 처리하여 조회된다.
  - 가장 많이 스트리밍된 음원 순서대로 순위를 제공한다.
  - 1시간, 1일, 1주일, 1달을 집계 기준으로하여 원하는 차트 순위 제공
  - 집계는 스트리밍 로그를 이용하여 집계하고, 음원을 50% 이상 들은 로그만 집계에 포함한다.
  - 순위는 한 시간마다 갱신된다.
  - 집계에 사용되는 정보는 갱신되는 시점부터  전까지의 스트리밍 정보만을 이용한다.
 
- 플레이리스트 생성 기능
  - 일반 회원은 원하는 노래를 모아 놓을 수 있는 플레이리스트를 생성할 수 있다.
  - 플레이리스트의 제목을 등록할 수 있다.
  - 직접 생성한 플레이리스트는 본인만 이용할 수 있다.
  - 플레이리스트를 재생하면 플레이리스트 내에 있는 노래들만 순서대로 재생된다.
 
- 플레이리스트 삭제 기능
  - 일반회원은 플레이리스트를 삭제할 수 있다.
 
- 플레이리스트 음원 추가 기능
  - 일반회원은 플레이리스트에 음원을 추가할 수 있다.
  - 제일 먼저 추가된 순서대로 재생된다.
 
- 플레이리스트 음원 삭제 기능
  - 일반회원은 플레이리스트의 음원을 삭제할 수 있다.
 
- 검색 기능
  - 검색 키워드와 선택한 카테고리를 기준으로 내림차순으로 노출 (아티스트, 앨범, 음원 모두 통합하여 검색)
  - 카테고리 : 인기순, 최근, 정확도

- 솔로 아티스트 등록 기능
  - 관리자 회원만 등록할 수 있다.
  - 활동명, 프로필 이미지, 설명, 소속사, 데뷔일을 등록할 수 있다. 프로필 이미지, 설명, 소속사는 null일 수 있다.
 
- 그룹 아티스트 등록 기능
  - 관리자 회원만 등록할 수 있다.
  - 그룹에 속할 아티스트 정보(ID, 포지션), 활동명, 프로필 이미지, 설명, 소속사, 데뷔일을 등록할 수 있다. 프로필 이미지, 설명, 소속사는 null일 수 있다.
  - 그룹에 속할 아티스트는 '솔로 아티스트'에 등록돼 있어야 한다.
  
- 아티스트 조회 기능
  - 모든 회원이 아티스트를 조회할 수 있다.
  - 아티스트의 상태가 'ACTIVE'인 아티스트만 조회된다.
  - 해당 아티스트의 앨범, 활동명, 프로필 이미지, 설명, 소속사, 데뷔일이 조회된다.
  - 솔로 아티스트라면 모든 소속 그룹도 함께 조회된다.
  - 그룹이라면 소속 멤버와 각 멤버의 포지션도 함께 조회된다.

- 아티스트 수정 기능
  - 관리자 회원만 수정할 수 있다.
  - 활동명, 프로필 이미지, 설명, 소속사, 데뷔일, 상태를 수정할 수 있다.
 
- 아티스트 삭제 기능
  - 관리자 회원만 삭제할 수 있다.
  - 아티스트의 상태를 'DELETED'로 변경
  - DB에서 삭제하지 않음

- 앨범 조회 기능
  - 모든 회원이 앨범을 조회할 수 있다.
  - 앨범의 발매 상태가 'RELEASED'인 앨범만 조회된다.
  - 활동명, 포함된 음원, 앨범 제목, 앨범 커버 이미지, 장르, 설명, 앨범 타입, 발매일이 조회된다.
 
- 앨범 업로드 기능
  - 관리자 회원만 앨범을 등록할 수 있다.
  - 활동명, 앨범 제목, 앨범 커버 이미지, 장르, 설명, 앨범 타입, 발매일을 등록할 수 있다. 앨범 커버 이미지, 설명은 null일 수 있다.
  - 앨범의 아티스트 상태가 'ACTIVE'이어야 한다.
  - 앨범을 등록하면 앨범의 상태는 'PENDING'이다.
  - 등록한 발매일 오후 6시에 발매 상태가 'PENDING'인 앨범의 발매 상태를 'RELEASED'로 변경한다.

- 앨범 수정 기능
  - 관리자 회원만 앨범을 수정할 수 있다.
  - 앨범 제목, 앨범 커버 이미지, 장르, 설명, 앨범 타입, 발매일, 상태를 수정할 수 있다.

- 앨범 삭제 기능
  - 관리자 회원만 앨범을 삭제할 수 있다.
  - 앨범의 상태를 'DELETED'로 변경
  - DB에서 삭제하지 않음

- 음원 조회 기능
  - 모든 회원이 음원을 조회할 수 있다.
  - 음악의 발매 상태가 'RELEASED'인 음악만 조회된다.
  - 제목, 활동명, 지속시간, 발매일, 장르, 타이틀곡 여부가 조회된다.
 
- 음원 업로드 기능
  - 관리자 회원만 음원을 업로드할 수 있다.
  - 음원 파일, 가사, 제목, 트랙 번호, 발매일, 장르, 타이틀곡 여부를 등록할 수 있다. 가사는 null일 수 있다.
  - 음원이 속할 앨범의 상태가 'PENDIG' 이거나 'RELEASED'이어야 한다.
  - 음원을 등록하면 음원의 상태는 'PENDING'이다.
  - 등록한 발매일 오후 6시에 발매 상태가 'PENDING'인 음원의 발매 상태를 'RELEASED'로 변경한다.
 
- 음원 수정 기능
  - 관리자 회원만 음원을 수정할 수 있다.
  - 음원 상태가 PENDIG' 이거나 'RELEASED'이어야 한다.
  - 음원, 가사, 제목, 발매일, 장르, 타이틀곡 여부, 상태를 수정할 수 있다.
  - 현재 상태가 PENDING이고 수정한 발매일이 오늘보다 이전이라면 상태를 'RELEASED'로 변경

- 음원 삭제 기능
  - 관리자 회원만 음원을 삭제할 수 있다.
  - 음원의 상태를 'DELETED'로 변경
  - DB에서 삭제하지 않음

## ERD
![music_ERD](https://github.com/user-attachments/assets/1163b994-0a16-416b-bc90-056421a1e714)

## Trouble Shooting

## Tech Stack
- Backend
  - Java 11
  - Spring Boot 2.7.18
  - Spring Data JPA

- Database
  - MySQL: 사용자/음원/앨범/음원 등 핵심 관계 데이터 관리
  - MongoDB: 스트리밍 로그 관리
  - AWS S3: 음원 파일, 가사 파일, 이미지 파일 등 저장
  - Elasticsearch: 통합 검색 기능
  - Redis: 캐싱 기능
