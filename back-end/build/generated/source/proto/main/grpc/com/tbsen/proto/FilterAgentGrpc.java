package com.tbsen.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Services //
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.62.2)",
    comments = "Source: filter.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FilterAgentGrpc {

  private FilterAgentGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.tbsen.proto.FilterAgent";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.tbsen.proto.AgentIdentity,
      com.tbsen.proto.CommandResponse> getRegisterAgentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterAgent",
      requestType = com.tbsen.proto.AgentIdentity.class,
      responseType = com.tbsen.proto.CommandResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.tbsen.proto.AgentIdentity,
      com.tbsen.proto.CommandResponse> getRegisterAgentMethod() {
    io.grpc.MethodDescriptor<com.tbsen.proto.AgentIdentity, com.tbsen.proto.CommandResponse> getRegisterAgentMethod;
    if ((getRegisterAgentMethod = FilterAgentGrpc.getRegisterAgentMethod) == null) {
      synchronized (FilterAgentGrpc.class) {
        if ((getRegisterAgentMethod = FilterAgentGrpc.getRegisterAgentMethod) == null) {
          FilterAgentGrpc.getRegisterAgentMethod = getRegisterAgentMethod =
              io.grpc.MethodDescriptor.<com.tbsen.proto.AgentIdentity, com.tbsen.proto.CommandResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterAgent"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.AgentIdentity.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.CommandResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FilterAgentMethodDescriptorSupplier("RegisterAgent"))
              .build();
        }
      }
    }
    return getRegisterAgentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.tbsen.proto.AgentStatus,
      com.tbsen.proto.CommandResponse> getReportStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReportStatus",
      requestType = com.tbsen.proto.AgentStatus.class,
      responseType = com.tbsen.proto.CommandResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.tbsen.proto.AgentStatus,
      com.tbsen.proto.CommandResponse> getReportStatusMethod() {
    io.grpc.MethodDescriptor<com.tbsen.proto.AgentStatus, com.tbsen.proto.CommandResponse> getReportStatusMethod;
    if ((getReportStatusMethod = FilterAgentGrpc.getReportStatusMethod) == null) {
      synchronized (FilterAgentGrpc.class) {
        if ((getReportStatusMethod = FilterAgentGrpc.getReportStatusMethod) == null) {
          FilterAgentGrpc.getReportStatusMethod = getReportStatusMethod =
              io.grpc.MethodDescriptor.<com.tbsen.proto.AgentStatus, com.tbsen.proto.CommandResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReportStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.AgentStatus.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.CommandResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FilterAgentMethodDescriptorSupplier("ReportStatus"))
              .build();
        }
      }
    }
    return getReportStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.tbsen.proto.AgentIdentity,
      com.tbsen.proto.Command> getSubscribeCommandsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeCommands",
      requestType = com.tbsen.proto.AgentIdentity.class,
      responseType = com.tbsen.proto.Command.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.tbsen.proto.AgentIdentity,
      com.tbsen.proto.Command> getSubscribeCommandsMethod() {
    io.grpc.MethodDescriptor<com.tbsen.proto.AgentIdentity, com.tbsen.proto.Command> getSubscribeCommandsMethod;
    if ((getSubscribeCommandsMethod = FilterAgentGrpc.getSubscribeCommandsMethod) == null) {
      synchronized (FilterAgentGrpc.class) {
        if ((getSubscribeCommandsMethod = FilterAgentGrpc.getSubscribeCommandsMethod) == null) {
          FilterAgentGrpc.getSubscribeCommandsMethod = getSubscribeCommandsMethod =
              io.grpc.MethodDescriptor.<com.tbsen.proto.AgentIdentity, com.tbsen.proto.Command>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeCommands"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.AgentIdentity.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.Command.getDefaultInstance()))
              .setSchemaDescriptor(new FilterAgentMethodDescriptorSupplier("SubscribeCommands"))
              .build();
        }
      }
    }
    return getSubscribeCommandsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.tbsen.proto.CommandResponse,
      com.tbsen.proto.CommandResponse> getReportCommandResultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReportCommandResult",
      requestType = com.tbsen.proto.CommandResponse.class,
      responseType = com.tbsen.proto.CommandResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.tbsen.proto.CommandResponse,
      com.tbsen.proto.CommandResponse> getReportCommandResultMethod() {
    io.grpc.MethodDescriptor<com.tbsen.proto.CommandResponse, com.tbsen.proto.CommandResponse> getReportCommandResultMethod;
    if ((getReportCommandResultMethod = FilterAgentGrpc.getReportCommandResultMethod) == null) {
      synchronized (FilterAgentGrpc.class) {
        if ((getReportCommandResultMethod = FilterAgentGrpc.getReportCommandResultMethod) == null) {
          FilterAgentGrpc.getReportCommandResultMethod = getReportCommandResultMethod =
              io.grpc.MethodDescriptor.<com.tbsen.proto.CommandResponse, com.tbsen.proto.CommandResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReportCommandResult"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.CommandResponse.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.tbsen.proto.CommandResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FilterAgentMethodDescriptorSupplier("ReportCommandResult"))
              .build();
        }
      }
    }
    return getReportCommandResultMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FilterAgentStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FilterAgentStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FilterAgentStub>() {
        @java.lang.Override
        public FilterAgentStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FilterAgentStub(channel, callOptions);
        }
      };
    return FilterAgentStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FilterAgentBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FilterAgentBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FilterAgentBlockingStub>() {
        @java.lang.Override
        public FilterAgentBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FilterAgentBlockingStub(channel, callOptions);
        }
      };
    return FilterAgentBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FilterAgentFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FilterAgentFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FilterAgentFutureStub>() {
        @java.lang.Override
        public FilterAgentFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FilterAgentFutureStub(channel, callOptions);
        }
      };
    return FilterAgentFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Services //
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * 등록 (에이전트 -&gt; 서버)
     * </pre>
     */
    default void registerAgent(com.tbsen.proto.AgentIdentity request,
        io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterAgentMethod(), responseObserver);
    }

    /**
     * <pre>
     * 상태보고 (에이전트 -&gt; 서버)
     * </pre>
     */
    default io.grpc.stub.StreamObserver<com.tbsen.proto.AgentStatus> reportStatus(
        io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getReportStatusMethod(), responseObserver);
    }

    /**
     * <pre>
     * 명령 수신 (서버 -&gt; 에이전트)
     * </pre>
     */
    default void subscribeCommands(com.tbsen.proto.AgentIdentity request,
        io.grpc.stub.StreamObserver<com.tbsen.proto.Command> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeCommandsMethod(), responseObserver);
    }

    /**
     * <pre>
     * 명령 결과 보고 (에이전트 -&gt; 서버)
     * </pre>
     */
    default void reportCommandResult(com.tbsen.proto.CommandResponse request,
        io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReportCommandResultMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FilterAgent.
   * <pre>
   * Services //
   * </pre>
   */
  public static abstract class FilterAgentImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FilterAgentGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FilterAgent.
   * <pre>
   * Services //
   * </pre>
   */
  public static final class FilterAgentStub
      extends io.grpc.stub.AbstractAsyncStub<FilterAgentStub> {
    private FilterAgentStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FilterAgentStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FilterAgentStub(channel, callOptions);
    }

    /**
     * <pre>
     * 등록 (에이전트 -&gt; 서버)
     * </pre>
     */
    public void registerAgent(com.tbsen.proto.AgentIdentity request,
        io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterAgentMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 상태보고 (에이전트 -&gt; 서버)
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.tbsen.proto.AgentStatus> reportStatus(
        io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getReportStatusMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * 명령 수신 (서버 -&gt; 에이전트)
     * </pre>
     */
    public void subscribeCommands(com.tbsen.proto.AgentIdentity request,
        io.grpc.stub.StreamObserver<com.tbsen.proto.Command> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeCommandsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 명령 결과 보고 (에이전트 -&gt; 서버)
     * </pre>
     */
    public void reportCommandResult(com.tbsen.proto.CommandResponse request,
        io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReportCommandResultMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FilterAgent.
   * <pre>
   * Services //
   * </pre>
   */
  public static final class FilterAgentBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FilterAgentBlockingStub> {
    private FilterAgentBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FilterAgentBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FilterAgentBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 등록 (에이전트 -&gt; 서버)
     * </pre>
     */
    public com.tbsen.proto.CommandResponse registerAgent(com.tbsen.proto.AgentIdentity request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterAgentMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 명령 수신 (서버 -&gt; 에이전트)
     * </pre>
     */
    public java.util.Iterator<com.tbsen.proto.Command> subscribeCommands(
        com.tbsen.proto.AgentIdentity request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeCommandsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 명령 결과 보고 (에이전트 -&gt; 서버)
     * </pre>
     */
    public com.tbsen.proto.CommandResponse reportCommandResult(com.tbsen.proto.CommandResponse request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReportCommandResultMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FilterAgent.
   * <pre>
   * Services //
   * </pre>
   */
  public static final class FilterAgentFutureStub
      extends io.grpc.stub.AbstractFutureStub<FilterAgentFutureStub> {
    private FilterAgentFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FilterAgentFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FilterAgentFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 등록 (에이전트 -&gt; 서버)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.tbsen.proto.CommandResponse> registerAgent(
        com.tbsen.proto.AgentIdentity request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterAgentMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 명령 결과 보고 (에이전트 -&gt; 서버)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.tbsen.proto.CommandResponse> reportCommandResult(
        com.tbsen.proto.CommandResponse request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReportCommandResultMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_AGENT = 0;
  private static final int METHODID_SUBSCRIBE_COMMANDS = 1;
  private static final int METHODID_REPORT_COMMAND_RESULT = 2;
  private static final int METHODID_REPORT_STATUS = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER_AGENT:
          serviceImpl.registerAgent((com.tbsen.proto.AgentIdentity) request,
              (io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_COMMANDS:
          serviceImpl.subscribeCommands((com.tbsen.proto.AgentIdentity) request,
              (io.grpc.stub.StreamObserver<com.tbsen.proto.Command>) responseObserver);
          break;
        case METHODID_REPORT_COMMAND_RESULT:
          serviceImpl.reportCommandResult((com.tbsen.proto.CommandResponse) request,
              (io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REPORT_STATUS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.reportStatus(
              (io.grpc.stub.StreamObserver<com.tbsen.proto.CommandResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRegisterAgentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.tbsen.proto.AgentIdentity,
              com.tbsen.proto.CommandResponse>(
                service, METHODID_REGISTER_AGENT)))
        .addMethod(
          getReportStatusMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              com.tbsen.proto.AgentStatus,
              com.tbsen.proto.CommandResponse>(
                service, METHODID_REPORT_STATUS)))
        .addMethod(
          getSubscribeCommandsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.tbsen.proto.AgentIdentity,
              com.tbsen.proto.Command>(
                service, METHODID_SUBSCRIBE_COMMANDS)))
        .addMethod(
          getReportCommandResultMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.tbsen.proto.CommandResponse,
              com.tbsen.proto.CommandResponse>(
                service, METHODID_REPORT_COMMAND_RESULT)))
        .build();
  }

  private static abstract class FilterAgentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FilterAgentBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.tbsen.proto.FilterProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FilterAgent");
    }
  }

  private static final class FilterAgentFileDescriptorSupplier
      extends FilterAgentBaseDescriptorSupplier {
    FilterAgentFileDescriptorSupplier() {}
  }

  private static final class FilterAgentMethodDescriptorSupplier
      extends FilterAgentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    FilterAgentMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FilterAgentGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FilterAgentFileDescriptorSupplier())
              .addMethod(getRegisterAgentMethod())
              .addMethod(getReportStatusMethod())
              .addMethod(getSubscribeCommandsMethod())
              .addMethod(getReportCommandResultMethod())
              .build();
        }
      }
    }
    return result;
  }
}
