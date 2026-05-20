using Grpc.Core;

namespace grpc2026.Services
{
    public class DS26Service : DS26.DS26Base
    {
        public override async Task BidirectionalStreaming(IAsyncStreamReader<Number> requestStream, IServerStreamWriter<Number> responseStream, ServerCallContext context)
        {
            await foreach (var number in requestStream.ReadAllAsync())
            { 
                await responseStream.WriteAsync(number);
            }
        }

        public override async Task<Number> ClientStreaming(IAsyncStreamReader<Number> requestStream, ServerCallContext context)
        {
            int sum = 0;

            await foreach (var number in requestStream.ReadAllAsync())
            {
                sum += number.Value;
            }

            return await Task.FromResult(new Number { Value = sum });
        }

        public override async Task ServerStreaming(Number request, IServerStreamWriter<Number> responseStream, ServerCallContext context)
        {
            for (int i = 1; i <= request.Value; i++)
            {
                await responseStream.WriteAsync(new Number { Value = i });
            }
        }

        public override async Task<Number> Unary(Number request, ServerCallContext context)
        {
            return await Task.FromResult(new Number { Value = request.Value });
        }
    }
}
