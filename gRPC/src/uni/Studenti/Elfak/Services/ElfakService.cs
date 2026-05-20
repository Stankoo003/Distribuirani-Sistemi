using Google.Protobuf.WellKnownTypes;
using Grpc.Core;

namespace Elfak.Services
{
    public class ElfakService : Elfak.ElfakBase
    {
        public override Task<Poruka> DodajStudenta(Student request, ServerCallContext context)
        {
            if (Studenti.Instanca().Baza.ContainsKey(request.BrojIndeksa))
            {
                return Task.FromResult(new Poruka { Text = "Student vec postoji" });
            }

            Studenti.Instanca().Baza.Add(request.BrojIndeksa, request.ImePrezime);

            return Task.FromResult(new Poruka { Text = $"Student sa brojem indeksa {request.BrojIndeksa} uspesno dodat" });
        }
        public override async Task DodajStudente(IAsyncStreamReader<Student> requestStream, IServerStreamWriter<Poruka> responseStream, ServerCallContext context)
        {
            await foreach (var student in requestStream.ReadAllAsync())
            {
                if (Studenti.Instanca().Baza.ContainsKey(student.BrojIndeksa)) continue;
                Studenti.Instanca().Baza.Add(student.BrojIndeksa, student.ImePrezime);

                await responseStream.WriteAsync(new Poruka { Text = $"Student sa brojem indeksa {student.BrojIndeksa} uspesno dodat" });
            }
        }

        public override Task<Empty> ObrisiStudenta(Indeks request, ServerCallContext context)
        {
            if (!Studenti.Instanca().Baza.ContainsKey(request.BrojIndeksa))
            {
                return Task.FromResult(new Empty());
            }

            Studenti.Instanca().Baza.Remove(request.BrojIndeksa);

            return Task.FromResult(new Empty());
        }

        public override async Task<Empty> ObrisiStudente(IAsyncStreamReader<Indeks> requestStream, ServerCallContext context)
        {
            await foreach (var brojIndeksa in requestStream.ReadAllAsync())
            {
                if (!Studenti.Instanca().Baza.ContainsKey(brojIndeksa.BrojIndeksa)) continue;

                Studenti.Instanca().Baza.Remove(brojIndeksa.BrojIndeksa);
            }

            return await Task.FromResult(new Empty());
        }

        public override Task<Student> PreuzmiStudenta(Indeks request, ServerCallContext context)
        {
            if (!Studenti.Instanca().Baza.ContainsKey(request.BrojIndeksa))
            {
                return Task.FromResult(new Student());
            }

            return Task.FromResult(new Student()
            {
                BrojIndeksa = request.BrojIndeksa,
                ImePrezime = Studenti.Instanca().Baza.GetValueOrDefault(request.BrojIndeksa)
            });
        }

        public override async Task PreuzmiStudente(IndeksOdDo request, IServerStreamWriter<Student> responseStream, ServerCallContext context)
        {
            var studenti = Studenti.Instanca().Baza.OrderBy(s => s.Key).Where(s => s.Key >= request.OdBrojaIndeksa && s.Key <= request.DoBrojaIndeksa);

            foreach (var sv in studenti)
            {
                await responseStream.WriteAsync(new Student
                {
                    BrojIndeksa = sv.Key,
                    ImePrezime = sv.Value
                });
            }
        }
    }
}
