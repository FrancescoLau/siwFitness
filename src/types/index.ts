export interface User {
  id: number;
  nome: string;
  cognome: string;
}

export interface Recensione {
  id: number;
  voto: number;
  testo: string;
  dataCreazione: string;
  utente?: User;
}

export interface Allenamento {
  id: number;
  nome: string;
  tipoSport: string;
  livelloDifficolta?: string;
  data: string;
  durata: string;
  descrizione?: string;
  isPubblico: boolean;
  utente?: User;
  recensioni?: Recensione[];
}