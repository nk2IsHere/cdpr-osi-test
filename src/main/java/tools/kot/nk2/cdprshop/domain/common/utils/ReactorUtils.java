package tools.kot.nk2.cdprshop.domain.common.utils;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

public class ReactorUtils {

    public static  <T> Mono<T> async(Callable<T> callable) {
        return Mono.fromCallable(callable)
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel());
    }
}
