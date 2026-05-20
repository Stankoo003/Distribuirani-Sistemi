using ElfakClient;
using Grpc.Core;
using Grpc.Net.Client;

using var channel = GrpcChannel.ForAddress("http://localhost:5000");
var client = new Elfak.ElfakClient(channel);

string unos;

do 
{
    Console.WriteLine("\n\tDodaj studenta - a1");
    Console.WriteLine("\tDodaj vise studenata - am");
    Console.WriteLine("\tObrisi studenta - d1");
    Console.WriteLine("\tObrisi vise studenata - dm");
    Console.WriteLine("\tPreuzmi studenta - g1");
    Console.WriteLine("\tPreuzmi vise studenata - gm");
    Console.WriteLine("\tPrekini izvrsenje programa - x");
    unos = Console.ReadLine();
    switch (unos)
    {
        case "a1":
            await DodajStudenta();
            break;
        case "am":
            await DodajStudente();
            break;
        case "d1": 
            await ObrisiStudenta();
            break;
        case "dm":
            await ObrisiStudente();
            break;
        case "g1":
            await PreuzmiStudenta();
            break;
        case "gm":
            await PreuzmiStudente();
            break;
        case "x": break;
        default: break;
    }
} while (unos != "x") ;

async Task DodajStudenta()
{
    Console.WriteLine("Unesite broj indeksa, a zatim ime i prezime studenta.");
    int brojIndeksa = Int32.Parse(Console.ReadLine());
    string imePrezime = Console.ReadLine();

    try
    {
        var resp = await client.DodajStudentaAsync(new Student
        {
            BrojIndeksa = brojIndeksa,
            ImePrezime = imePrezime
        });
        Console.WriteLine(resp.Text);
    }
    catch (Exception e)
    {
        Console.WriteLine(e);
    }
}

async Task DodajStudente()
{
    Console.WriteLine("Unesite broj indeksa, a zatim ime i prezime studenta.\nUnesite \"1\" za broj indeksa da prekinete sa unosom.");

    var call = client.DodajStudente();

    try
    {
        var pozadinskiTask = Task.Run(async () =>
        {
            await foreach (var resp in call.ResponseStream.ReadAllAsync())
            {
                Console.WriteLine(resp.Text);
            }
        });

        while (true)
        {
            int brojIndeksa = Int32.Parse(Console.ReadLine());
            if (brojIndeksa == 1) break;
            string imePrezime = Console.ReadLine();

            await call.RequestStream.WriteAsync(new Student
            {
                BrojIndeksa = brojIndeksa,
                ImePrezime = imePrezime
            });
        }

        await call.RequestStream.CompleteAsync();
        await pozadinskiTask;
    }
    catch (Exception e)
    {
        Console.WriteLine(e);
    }
}

async Task ObrisiStudenta()
{
    Console.WriteLine("Unesite broj indeksa studenta.");
    int brojIndeksa = Int32.Parse(Console.ReadLine());

    try
    {
        var resp = await client.ObrisiStudentaAsync(new Indeks
        {
            BrojIndeksa = brojIndeksa
        });
    }
    catch (Exception e)
    {
        Console.WriteLine(e);
    }
}

async Task ObrisiStudente()
{
    Console.WriteLine("Unesite broj indeksa studenta.\nUnesite \"1\" za broj indeksa da prekinete sa unosom.");
    try
    {
        var call = client.ObrisiStudente();
        while (true)
        {
            int brojIndeksa = Int32.Parse(Console.ReadLine());
            if (brojIndeksa == 1) break;

            await call.RequestStream.WriteAsync(new Indeks
            {
                BrojIndeksa = brojIndeksa
            });
        }

        await call.RequestStream.CompleteAsync();

        var resp = await call;
    }
    catch (Exception e)
    {
        Console.WriteLine(e);
    }
}

async Task PreuzmiStudenta()
{
    Console.WriteLine("Unesite broj indeksa studenta.");
    int brojIndeksa = Int32.Parse(Console.ReadLine());

    try
    {
        var resp = await client.PreuzmiStudentaAsync(new Indeks
        {
            BrojIndeksa = brojIndeksa
        });
        Console.WriteLine($"{resp.BrojIndeksa} {resp.ImePrezime}");
    }
    catch (Exception e)
    {
        Console.WriteLine(e);
    }
}

async Task PreuzmiStudente()
{
    Console.WriteLine("Unesite donju i gornju granicu broja indeksa.");
    int odBrojaIndeksa = Int32.Parse(Console.ReadLine());
    int doBrojIndeksa = Int32.Parse(Console.ReadLine());
    try
    {
        var call = client.PreuzmiStudente(new IndeksOdDo
        {
            OdBrojaIndeksa = odBrojaIndeksa,
            DoBrojaIndeksa = doBrojIndeksa
        });

        await foreach (var resp in call.ResponseStream.ReadAllAsync())
        {
            Console.WriteLine($"{resp.BrojIndeksa} {resp.ImePrezime}");
        }
    }
    catch (Exception e)
    {
        Console.WriteLine(e);
    }
}