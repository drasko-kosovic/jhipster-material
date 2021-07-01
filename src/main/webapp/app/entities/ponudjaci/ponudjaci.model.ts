export interface IPonudjaci {
  id?: number;
  ime?: string;
}

export class Ponudjaci implements IPonudjaci {
  constructor(public id?: number, public ime?: string) {}
}

export function getPonudjaciIdentifier(ponudjaci: IPonudjaci): number | undefined {
  return ponudjaci.id;
}
