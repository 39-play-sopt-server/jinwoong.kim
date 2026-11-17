-- 소셜 전용 회원의 비밀번호 없는 가입을 위한 기존 컬럼 제약 완화
-- 대상 DB를 확인한 뒤 1회 적용; 기존 비밀번호 해시 데이터 유지
ALTER TABLE member MODIFY COLUMN password VARCHAR(255) NULL;
