using Grpc.Core;
using Grpc.Net.Client;
using System.Net.WebSockets;
using System.Windows.Markup;

var channel = GrpcChannel.ForAddress("http://localhost:5000");
var client = new Waver.WaverClient(channel);

while (true)
{
    Console.WriteLine("1 - Unary\n2 - Client Streaming\n3 - Server Streaming\n4 - Bidirectional Streaming\n");
    var input = Console.ReadLine();
    switch (int.Parse(input))
    {
        case 1:
            var response1 = await client.UnaryCallAsync(new WaverData { Number = 1 });
            Console.WriteLine($"Unary call response: {response1.Number}");
            break;
        case 2:
            var call2 = client.ClientStreaming();
            for (int i = 0; i < 10; i++)
            {
                await call2.RequestStream.WriteAsync(new WaverData
                {
                    Number = i
                });
            }

            await call2.RequestStream.CompleteAsync();
            var response2 = await call2;
            Console.WriteLine($"Client Streaming response: {response2.Number}");
            break;
        case 3:
            var call3 = client.ServerStreaming(new WaverData { Number = 10 });

            Console.Write("Server streaming response: ");
            await foreach (var response3 in call3.ResponseStream.ReadAllAsync())
            {
                Console.Write($"{response3.Number} ");
            }
            Console.WriteLine();
            break;
        case 4:
            var call4 = client.BidirectionalStreaming();
            var backgroundTask = Task.Run(async () =>
            {
                await foreach (var response4 in call4.ResponseStream.ReadAllAsync())
                {
                    Console.WriteLine($"Server response: {response4.Number}");
                }
            });

            for (int i = 11; i <= 20; i++)
            {
                await call4.RequestStream.WriteAsync(new WaverData() { Number = i });
                Thread.Sleep(1000);
            }

            await call4.RequestStream.CompleteAsync();
            await backgroundTask;
            break;
        default:
            continue;
    }
}