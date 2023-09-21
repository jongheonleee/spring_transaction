package hello.springtx.exception;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;


/**
 * 스프링 AOP의 예외 발생에 따른 트랜잭션 처리
 * 런타임(언체크) 예외 -> 롤백
 * 체크 예외 -> 커밋
 *
 * Q. 예외가 발생했는데 왜 커밋할까?
 * A. 앱 운영 측면에서 발생하는 예외에는 시스템/ 비즈니스 예외가 있음
 *    시스템 -> 복구 x, 롤백
 *    비즈니스 -> 대안 o, 커밋(대기) -> 대안 제시
 */
@SpringBootTest
public class RollbackTest {

    @Autowired RollbackService service;

    @Test
    void runtimeException() {
        Assertions.assertThatThrownBy(() -> service.runtimeException())
                        .isInstanceOf(RuntimeException.class);
    }


    @Test
    void checkedException() {
        Assertions.assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    void rollbackFor() {
        Assertions.assertThatThrownBy(() -> service.rollbackFor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {

        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }


    @Slf4j
    static class RollbackService {
        // 런타임 예외 발생 - 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생 - 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }


        // 체크 예외 rollbackFor 지정 - 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
        }

    }

    static class MyException extends Exception {

    }
}
