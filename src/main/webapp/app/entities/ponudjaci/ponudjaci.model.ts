export interface IPonudjaci {
  id?: number;
  nazivPonudjaca?: string;
}

export class Ponudjaci implements IPonudjaci {
  constructor(public id?: number, public nazivPonudjaca?: string) {}
}

export function getPonudjaciIdentifier(ponudjaci: IPonudjaci): number | undefined {
  return ponudjaci.id;
}
