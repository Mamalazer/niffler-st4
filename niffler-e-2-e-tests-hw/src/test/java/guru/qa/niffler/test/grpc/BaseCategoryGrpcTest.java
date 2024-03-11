package guru.qa.niffler.test.grpc;

import guru.qa.grpc.niffler.grpc.NifflerCategoryServiceGrpc;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.grpc.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

public class BaseCategoryGrpcTest {
    protected static final Config CFG = Config.getInstance();

    protected static Channel channel;
    protected static NifflerCategoryServiceGrpc.NifflerCategoryServiceBlockingStub blockingStub;
    protected static NifflerCategoryServiceGrpc.NifflerCategoryServiceStub stub;

    static {
        channel = ManagedChannelBuilder.forAddress(CFG.spendGrpcHost(), CFG.spendGrpcPort())
                .intercept(new AllureGrpc(), new GrpcConsoleInterceptor())
                .usePlaintext()
                .build();

        blockingStub = NifflerCategoryServiceGrpc.newBlockingStub(channel);
        stub = NifflerCategoryServiceGrpc.newStub(channel);
    }
}
