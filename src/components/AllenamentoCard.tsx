import { useState } from 'react';
import type { Allenamento } from '../types';

interface Props {
  allenamento: Allenamento;
}

export default function AllenamentoCard({ allenamento }: Props) {
  const [mostraRecensioni, setMostraRecensioni] = useState(false);
  const recensioni = allenamento.recensioni || [];

  return (
    <div
      style={{
        backgroundColor: '#ffffff',
        border: '1px solid #e2e8f0',
        borderRadius: '8px',
        padding: '1.5rem',
        marginBottom: '1.25rem',
        boxShadow: '0 2px 4px rgba(0,0,0,0.04)',
      }}
    >
      <h2 style={{ margin: '0 0 1rem 0', fontSize: '1.4rem', color: '#0f172a' }}>
        {allenamento.nome}
      </h2>

      {/* Recensioni ricevute */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.4rem', fontSize: '0.95rem' }}>
        <span style={{ fontWeight: 600, color: '#475569' }}>Recensioni ricevute:</span>
        <span style={{ color: '#0f172a' }}>{recensioni.length}</span>
      </div>

      {/* Sport */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.4rem', fontSize: '0.95rem' }}>
        <span style={{ fontWeight: 600, color: '#475569' }}>Sport:</span>
        <span style={{ color: '#0f172a' }}>{allenamento.tipoSport}</span>
      </div>

      {/* Difficoltà */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.4rem', fontSize: '0.95rem' }}>
        <span style={{ fontWeight: 600, color: '#475569' }}>Difficoltà:</span>
        <span style={{ color: '#0f172a' }}>{allenamento.livelloDifficolta || 'Non specificata'}</span>
      </div>

      {/* Durata */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.4rem', fontSize: '0.95rem' }}>
        <span style={{ fontWeight: 600, color: '#475569' }}>Durata:</span>
        <span style={{ color: '#0f172a' }}>{allenamento.durata}</span>
      </div>

      {/* Descrizione */}
      {allenamento.descrizione && (
        <p style={{ margin: '0.75rem 0', color: '#334155', lineHeight: 1.5, fontSize: '0.95rem' }}>
          {allenamento.descrizione}
        </p>
      )}

      {/* Azione: Visualizza Recensioni */}
      <div style={{ marginTop: '1rem' }}>
        <button
          onClick={() => setMostraRecensioni(!mostraRecensioni)}
          style={{
            backgroundColor: '#475569',
            color: '#ffffff',
            border: 'none',
            borderRadius: '6px',
            padding: '0.5rem 1rem',
            cursor: 'pointer',
            fontSize: '0.9rem',
            fontWeight: 500,
          }}
        >
          {mostraRecensioni ? 'Nascondi Recensioni' : 'Visualizza Recensioni'}
        </button>

        {mostraRecensioni && (
          <div style={{ marginTop: '1rem', borderTop: '1px solid #e2e8f0', paddingTop: '0.75rem' }}>
            {recensioni.length === 0 ? (
              <p style={{ fontStyle: 'italic', color: '#64748b', margin: '0.5rem 0' }}>
                Nessuna recensione disponibile per questo allenamento.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {recensioni.map((rec) => (
                  <div
                    key={rec.id}
                    style={{
                      backgroundColor: '#f8fafc',
                      border: '1px solid #f1f5f9',
                      padding: '0.75rem',
                      borderRadius: '6px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                      <strong style={{ color: '#0f172a' }}>
                        {rec.utente ? `${rec.utente.nome} ${rec.utente.cognome}` : 'Utente'}
                      </strong>
                      <span style={{ color: '#d97706', fontWeight: 700 }}>{rec.voto}/5 ⭐</span>
                    </div>
                    <p style={{ margin: 0, color: '#334155', fontSize: '0.9rem' }}>{rec.testo}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}