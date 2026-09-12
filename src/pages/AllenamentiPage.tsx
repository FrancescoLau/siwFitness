import { useEffect, useState } from 'react';
import type { Allenamento } from '../types';
import AllenamentoCard from '../components/AllenamentoCard';

export default function AllenamentiPage() {
  const [allenamenti, setAllenamenti] = useState<Allenamento[]>([]);
  const [loading, setLoading] = useState(true);

  const caricaAllenamentiConsigliati = () => {
    fetch('http://localhost:8080/rest/allenamenti')
      .then((res) => res.json())
      .then((data: any[]) => {
        // Mantiene solo gli allenamenti consigliati (isPubblico == true)
        const consigliati = data.filter((a) => {
          const pub = a.isPubblico !== undefined ? a.isPubblico : a.pubblico;
          return pub === true || pub === 'true';
        });
        setAllenamenti(consigliati);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Errore recupero allenamenti:', err);
        setLoading(false);
      });
  };

  useEffect(() => {
    caricaAllenamentiConsigliati();
    const interval = setInterval(caricaAllenamentiConsigliati, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f8fafc', fontFamily: 'sans-serif', padding: '2rem 1rem' }}>
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        
        {/* Header di pagina fedele al template */}
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h1 style={{ color: '#0f172a', fontSize: '2rem', margin: '0 0 0.5rem 0' }}>
            Allenamenti Consigliati
          </h1>
          <p style={{ color: '#64748b', margin: 0, fontSize: '1rem' }}>
            Scopri e segui i programmi di allenamento raccomandati dai nostri coach
          </p>
        </div>

        {/* Stato Caricamento */}
        {loading && (
          <p style={{ textAlign: 'center', color: '#64748b' }}>Caricamento in corso...</p>
        )}

        {/* Avviso Nessun Risultato */}
        {!loading && allenamenti.length === 0 && (
          <div style={{ backgroundColor: '#ffffff', padding: '2rem', borderRadius: '8px', textAlign: 'center' }}>
            <p style={{ fontStyle: 'italic', color: '#64748b', margin: 0 }}>
              Nessun allenamento consigliato disponibile al momento.
            </p>
          </div>
        )}

        {/* Lista Cards Allenamenti Consigliati */}
        {!loading && allenamenti.length > 0 && (
          <div>
            {allenamenti.map((allenamento) => (
              <AllenamentoCard key={allenamento.id} allenamento={allenamento} />
            ))}
          </div>
        )}

        {/* Navigazione Inferiore */}
        <div style={{ textAlign: 'center', marginTop: '2rem' }}>
          <a
            href="http://localhost:8080/"
            style={{
              display: 'inline-block',
              backgroundColor: '#475569',
              color: '#ffffff',
              padding: '0.6rem 1.2rem',
              borderRadius: '6px',
              textDecoration: 'none',
              fontSize: '0.95rem',
            }}
          >
            Torna alla Home
          </a>
        </div>

      </div>
    </div>
  );
}