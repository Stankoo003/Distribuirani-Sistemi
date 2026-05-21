using Grpc.Core;

namespace April26.Services;

public class KalkulatorService : Kalkulator.KalkulatorBase
{
    public override Task<KalkulatorOdgovor> Saberi(KalkulatorZahtev request, ServerCallContext context)
    {
        return Task.FromResult(new KalkulatorOdgovor
        {
            Operand1 = request.Operand1,
            Operand2 = request.Operand2,
            Operacija = "+",
            Rezultat = request.Operand1 + request.Operand2
        });
    }

    public override Task<KalkulatorOdgovor> Oduzmi(KalkulatorZahtev request, ServerCallContext context)
    {
        return Task.FromResult(new KalkulatorOdgovor
        {
            Operand1 = request.Operand1,
            Operand2 = request.Operand2,
            Operacija = "-",
            Rezultat = request.Operand1 - request.Operand2
        });
    }

    public override Task<KalkulatorOdgovor> Pomnozi(KalkulatorZahtev request, ServerCallContext context)
    {
        return Task.FromResult(new KalkulatorOdgovor
        {
            Operand1 = request.Operand1,
            Operand2 = request.Operand2,
            Operacija = "*",
            Rezultat = request.Operand1 * request.Operand2
        });
    }

    public override Task<KalkulatorOdgovor> Podeli(KalkulatorZahtev request, ServerCallContext context)
    {
        if (request.Operand2 == 0)
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Deljenje nulom nije dozvoljeno."));

        return Task.FromResult(new KalkulatorOdgovor
        {
            Operand1 = request.Operand1,
            Operand2 = request.Operand2,
            Operacija = "/",
            Rezultat = (double)request.Operand1 / request.Operand2
        });
    }
}
