import * as dayjs from 'dayjs';

export interface IUgovor {
  id?: number;
  brojUgovora?: string | null;
  datumUgovora?: dayjs.Dayjs | null;
  brojOdluke?: string | null;
  datumOdluke?: dayjs.Dayjs | null;
  iznosUgovoraBezPdf?: number | null;
  sifraPostupka?: number | null;
  sifraPonude?: number | null;
  sifraPonudjaca?: number | null;
}

export class Ugovor implements IUgovor {
  constructor(
    public id?: number,
    public brojUgovora?: string | null,
    public datumUgovora?: dayjs.Dayjs | null,
    public brojOdluke?: string | null,
    public datumOdluke?: dayjs.Dayjs | null,
    public iznosUgovoraBezPdf?: number | null,
    public sifraPostupka?: number | null,
    public sifraPonude?: number | null,
    public sifraPonudjaca?: number | null
  ) {}
}

export function getUgovorIdentifier(ugovor: IUgovor): number | undefined {
  return ugovor.id;
}
