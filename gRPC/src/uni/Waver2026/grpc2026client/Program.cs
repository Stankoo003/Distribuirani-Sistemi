using Grpc.Core;
using Grpc.Net.Client;
using grpc2026client;

try
{
    using var channel = GrpcChannel.ForAddress("http://localhost:5110");
    var client = new DS26.DS26Client(channel);
    #region Unary gRPC
    var resultUnary = client.Unary(new Number { Value = 10 });
    Console.WriteLine(resultUnary.Value);
    #endregion

    #region Client Streaming gRPC
    var callClientStreaming = client.ClientStreaming();
    for (int i = 1; i <= 10; i++)
        await callClientStreaming.RequestStream.WriteAsync(new Number { Value = i });
    await callClientStreaming.RequestStream.CompleteAsync();
    var resultClientStreaming = await callClientStreaming;
    Console.WriteLine(resultClientStreaming.Value);
    #endregion


    #region Server Streaming gRPC
    var resultServerStreaming = client.ServerStreaming(new Number { Value = 10 });
    await foreach (var result in resultServerStreaming.ResponseStream.ReadAllAsync())
        Console.Write(result.Value + " ");
    Console.WriteLine();
    #endregion

    #region Bidirectional Streaming gRPC
    var callBidirectionalStreaming = client.BidirectionalStreaming();
    var backgroundTask = Task.Run(async () =>
    {
        await foreach (var result in callBidirectionalStreaming.ResponseStream.ReadAllAsync())
            Console.Write(result.Value + " ");
        Console.WriteLine();
    });
    for (int i = 11; i <= 20; i++)
        await callBidirectionalStreaming.RequestStream.WriteAsync(new Number { Value = i });
    await callBidirectionalStreaming.RequestStream.CompleteAsync();
    await backgroundTask;
    #endregion
}
catch (Exception e)
{
    Console.WriteLine(e.Message);
}

Console.ReadLine();