import api from './api';
import type { Allenamento } from '../types';

export async function getAllenamenti(): Promise<Allenamento[]> {
  try {
    const { data } = await api.get<Allenamento[]>('/rest/allenamenti');
    return data;
  } catch (error) {
    console.error('Errore durante il caricamento degli allenamenti:', error);
    throw new Error('Impossibile caricare gli allenamenti dal server.');
  }
}