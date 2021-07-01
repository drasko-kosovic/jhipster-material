import { IPonudjaci } from 'app/entities/ponudjaci/ponudjaci.model';

export interface IPonude {
  id?: number;
  naziv?: string;
  ponudjaci?: IPonudjaci | null;
}

export class Ponude implements IPonude {
  constructor(public id?: number, public naziv?: string, public ponudjaci?: IPonudjaci | null) {}
}

export function getPonudeIdentifier(ponude: IPonude): number | undefined {
  return ponude.id;
}
