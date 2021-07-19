import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IUgovor, Ugovor } from '../ugovor.model';

import { UgovorService } from './ugovor.service';

describe('Service Tests', () => {
  describe('Ugovor Service', () => {
    let service: UgovorService;
    let httpMock: HttpTestingController;
    let elemDefault: IUgovor;
    let expectedResult: IUgovor | IUgovor[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(UgovorService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        brojUgovora: 'AAAAAAA',
        datumUgovora: currentDate,
        brojOdluke: 'AAAAAAA',
        datumOdluke: currentDate,
        iznosUgovoraBezPdf: 0,
        sifraPostupka: 0,
        sifraPonude: 0,
        sifraPonudjaca: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            datumUgovora: currentDate.format(DATE_FORMAT),
            datumOdluke: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Ugovor', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            datumUgovora: currentDate.format(DATE_FORMAT),
            datumOdluke: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            datumUgovora: currentDate,
            datumOdluke: currentDate,
          },
          returnedFromService
        );

        service.create(new Ugovor()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Ugovor', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            brojUgovora: 'BBBBBB',
            datumUgovora: currentDate.format(DATE_FORMAT),
            brojOdluke: 'BBBBBB',
            datumOdluke: currentDate.format(DATE_FORMAT),
            iznosUgovoraBezPdf: 1,
            sifraPostupka: 1,
            sifraPonude: 1,
            sifraPonudjaca: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            datumUgovora: currentDate,
            datumOdluke: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Ugovor', () => {
        const patchObject = Object.assign(
          {
            brojUgovora: 'BBBBBB',
            brojOdluke: 'BBBBBB',
            sifraPostupka: 1,
            sifraPonude: 1,
            sifraPonudjaca: 1,
          },
          new Ugovor()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            datumUgovora: currentDate,
            datumOdluke: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Ugovor', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            brojUgovora: 'BBBBBB',
            datumUgovora: currentDate.format(DATE_FORMAT),
            brojOdluke: 'BBBBBB',
            datumOdluke: currentDate.format(DATE_FORMAT),
            iznosUgovoraBezPdf: 1,
            sifraPostupka: 1,
            sifraPonude: 1,
            sifraPonudjaca: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            datumUgovora: currentDate,
            datumOdluke: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Ugovor', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addUgovorToCollectionIfMissing', () => {
        it('should add a Ugovor to an empty array', () => {
          const ugovor: IUgovor = { id: 123 };
          expectedResult = service.addUgovorToCollectionIfMissing([], ugovor);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(ugovor);
        });

        it('should not add a Ugovor to an array that contains it', () => {
          const ugovor: IUgovor = { id: 123 };
          const ugovorCollection: IUgovor[] = [
            {
              ...ugovor,
            },
            { id: 456 },
          ];
          expectedResult = service.addUgovorToCollectionIfMissing(ugovorCollection, ugovor);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Ugovor to an array that doesn't contain it", () => {
          const ugovor: IUgovor = { id: 123 };
          const ugovorCollection: IUgovor[] = [{ id: 456 }];
          expectedResult = service.addUgovorToCollectionIfMissing(ugovorCollection, ugovor);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(ugovor);
        });

        it('should add only unique Ugovor to an array', () => {
          const ugovorArray: IUgovor[] = [{ id: 123 }, { id: 456 }, { id: 18018 }];
          const ugovorCollection: IUgovor[] = [{ id: 123 }];
          expectedResult = service.addUgovorToCollectionIfMissing(ugovorCollection, ...ugovorArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const ugovor: IUgovor = { id: 123 };
          const ugovor2: IUgovor = { id: 456 };
          expectedResult = service.addUgovorToCollectionIfMissing([], ugovor, ugovor2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(ugovor);
          expect(expectedResult).toContain(ugovor2);
        });

        it('should accept null and undefined values', () => {
          const ugovor: IUgovor = { id: 123 };
          expectedResult = service.addUgovorToCollectionIfMissing([], null, ugovor, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(ugovor);
        });

        it('should return initial array if no Ugovor is added', () => {
          const ugovorCollection: IUgovor[] = [{ id: 123 }];
          expectedResult = service.addUgovorToCollectionIfMissing(ugovorCollection, undefined, null);
          expect(expectedResult).toEqual(ugovorCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
