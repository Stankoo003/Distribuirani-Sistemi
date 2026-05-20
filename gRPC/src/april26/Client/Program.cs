using April26;
using Grpc.Net.Client;


using var channel = GrpcChannel.ForAddress("http://localhost:5026");
var client = new Kalkulator.KalkulatorClient(channel);

void IspisiOdgovor(KalkulatorOdgovor odgovor)
{
    Console.WriteLine($"  {odgovor.Operand1} [{odgovor.Operacija}] {odgovor.Operand2} = {odgovor.Rezultat}");
}

Console.WriteLine("=== gRPC Kalkulator ===\n");

try
{
    var sabiranje = await client.SaberiAsync(new KalkulatorZahtev { Operand1 = 15, Operand2 = 7 });
    IspisiOdgovor(sabiranje);

    var oduzimanje = await client.OduzmiAsync(new KalkulatorZahtev { Operand1 = 15, Operand2 = 7 });
    IspisiOdgovor(oduzimanje);

    var mnozenje = await client.PomnoziAsync(new KalkulatorZahtev { Operand1 = 15, Operand2 = 7 });
    IspisiOdgovor(mnozenje);

    var deljenje = await client.PodeliAsync(new KalkulatorZahtev { Operand1 = 15, Operand2 = 7 });
    IspisiOdgovor(deljenje);

    // deljenje nulom — server baca RpcException
    Console.WriteLine("\nPokusaj deljenja sa nulom:");
    var deljenjeNulom = await client.PodeliAsync(new KalkulatorZahtev { Operand1 = 10, Operand2 = 0 });
    IspisiOdgovor(deljenjeNulom);
}
catch (Grpc.Core.RpcException ex)
{
    Console.WriteLine($"  gRPC greska: {ex.StatusCode} — {ex.Status.Detail}");
}
catch (Exception ex)
{
    Console.WriteLine($"  Greska: {ex.Message}");
}
