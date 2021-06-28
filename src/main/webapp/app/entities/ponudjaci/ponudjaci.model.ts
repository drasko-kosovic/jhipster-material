export interface IPonudjaci {
  id?: number;
  nazivPonudjaca?: string;
  datum?: Date;
}

export class Ponudjaci implements IPonudjaci {
  constructor(public id?: number, public nazivPonudjaca?: string, public datum?: Date) {}
}

export function getPonudjaciIdentifier(ponudjaci: IPonudjaci): number | undefined {
  return ponudjaci.id;
}
