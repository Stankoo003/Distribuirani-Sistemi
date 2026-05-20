namespace Elfak
{
    public class Studenti
    {
        public Dictionary<int, string> Baza { get; set; }
        private static Studenti instanca;
        private static object lockObj = new object();

        private Studenti() 
        { 
            Baza = new Dictionary<int, string>()
            {
                { 100, "Milos Milosevic" },
                { 105, "Mihajlo Mihajlovic" },
                { 107, "Djurdja Djuric" },
                { 110, "Marko Markovic" },
                { 120, "Milutin Misic" },
                { 121, "Milica Micic" }
            };
        }

        public static Studenti Instanca()
        {
            if (instanca == null)
            {
                lock (lockObj)
                {
                    if (instanca == null)
                    {
                        instanca = new Studenti();
                    }
                }
            }

            return instanca;
        }
        
    }
}
