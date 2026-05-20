using Grpc.Core;

namespace gRPC_Test_Server.Services
{
    public class WaverService : Waver.WaverBase
    {
        private int sum = 0;

        public override Task<WaverData> UnaryCall(WaverData request, ServerCallContext context)
        {
            Console.WriteLine($"This is {++sum}. call.");

            return Task.FromResult(new WaverData
            {
                Number = request.Number
            });
        }

        public override async Task<WaverData> ClientStreaming(IAsyncStreamReader<WaverData> requestStream, ServerCallContext context)
        {
            Console.WriteLine($"This is {++sum}. call.");

            int csSum = 0;
            await foreach (var item in requestStream.ReadAllAsync())
            {
                csSum += item.Number;
            }
            return await Task.FromResult(new WaverData
            {
                Number = csSum
            });
        }

        public override async Task ServerStreaming(WaverData request, IServerStreamWriter<WaverData> responseStream, ServerCallContext context)
        {
            Console.WriteLine($"This is {++sum}. call.");

            for (int i = request.Number; i > 0; i--)
            {
                await responseStream.WriteAsync(new WaverData 
                { 
                    Number = i 
                });
            }
        }

        public override async Task BidirectionalStreaming(IAsyncStreamReader<WaverData> requestStream, IServerStreamWriter<WaverData> responseStream, ServerCallContext context)
        {
            Console.WriteLine($"This is {++sum}. call.");

            await foreach (var item in requestStream.ReadAllAsync())
            {
                await responseStream.WriteAsync(new WaverData
                {
                    Number = item.Number + 1
                });
            }
        }
    }
}
