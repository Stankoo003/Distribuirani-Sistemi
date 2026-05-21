using Grpc.Core;

namespace Jun24.Services;

public class MessageService : Jun24.MessageService.MessageServiceBase
{
    private static readonly List<Message> _poruke = [];
    private static int _sledecId = 1;

    public override Task<SendMessageResponse> SendMessage(SendMessageRequest request, ServerCallContext context)
    {
        var poruka = new Message { Id = _sledecId++, Content = request.Content };
        _poruke.Add(poruka);
        Console.WriteLine($"Dodata poruka [{poruka.Id}]: {poruka.Content}");
        return Task.FromResult(new SendMessageResponse { Id = poruka.Id });
    }

    public override Task<DeleteMessageResponse> DeleteMessage(DeleteMessageRequest request, ServerCallContext context)
    {
        var poruka = _poruke.FirstOrDefault(p => p.Id == request.Id);
        if (poruka is null)
            return Task.FromResult(new DeleteMessageResponse { Success = false });

        _poruke.Remove(poruka);
        Console.WriteLine($"Obrisana poruka [{request.Id}]");
        return Task.FromResult(new DeleteMessageResponse { Success = true });
    }

    public override async Task ListMessages(ListMessagesRequest request, IServerStreamWriter<Message> responseStream, ServerCallContext context)
    {
        foreach (var poruka in _poruke)
            await responseStream.WriteAsync(poruka);
    }

    // Client streaming — klijent šalje tok poruka, server ih sve sačuva i vrati broj primljenih
    public override async Task<UploadResponse> UploadMessages(IAsyncStreamReader<SendMessageRequest> requestStream, ServerCallContext context)
    {
        int brojac = 0;
        while (await requestStream.MoveNext(CancellationToken.None))
        {
            var poruka = new Message { Id = _sledecId++, Content = requestStream.Current.Content };
            _poruke.Add(poruka);
            Console.WriteLine($"[Upload] Primljena poruka [{poruka.Id}]: {poruka.Content}");
            brojac++;
        }
        Console.WriteLine($"[Upload] Ukupno primljeno: {brojac}");
        return new UploadResponse { Sacuvano = brojac };
    }

    // Bidirectional streaming — klijent šalje poruke, server odmah odgovara na svaku
    public override async Task Chat(IAsyncStreamReader<SendMessageRequest> requestStream, IServerStreamWriter<Message> responseStream, ServerCallContext context)
    {
        while (await requestStream.MoveNext(CancellationToken.None))
        {
            var primljeno = requestStream.Current.Content;
            Console.WriteLine($"[Chat] Primio: {primljeno}");
            var odgovor = new Message { Id = _sledecId++, Content = $"Echo: {primljeno}" };
            _poruke.Add(odgovor);
            await responseStream.WriteAsync(odgovor);
        }
    }
}
