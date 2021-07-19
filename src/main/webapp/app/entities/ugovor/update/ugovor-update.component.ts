import {Component, Inject, OnInit} from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import {FormBuilder, FormGroup} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUgovor, Ugovor } from '../ugovor.model';
import { UgovorService } from '../service/ugovor.service';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'jhi-ugovor-update',
  templateUrl: './ugovor-update.component.html',
})
export class UgovorUpdateComponent implements OnInit {
  isSaving = false;
  editForm:FormGroup;


  constructor(protected ugovorService: UgovorService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder,
             public dialogRef: MatDialogRef<UgovorUpdateComponent>,
              @Inject(MAT_DIALOG_DATA)
                { brojUgovora, brojOdluke, datumOdluke, datumUgovora, iznosUgovoraBezPdf, sifraPostupka, sifraPonude, sifraPonudjaca, name }: any
  ) {
    this.editForm = this.fb.group({
      id: [],
      brojUgovora: [brojUgovora],
      datumUgovora: [datumUgovora],
      brojOdluke: [brojOdluke],
      datumOdluke: [datumOdluke],
      iznosUgovoraBezPdf: [iznosUgovoraBezPdf],
      sifraPostupka: [sifraPostupka],
      sifraPonude: [sifraPonude],
      sifraPonudjaca: [sifraPonudjaca],
    });
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ugovor }) => {
      this.updateForm(ugovor);
    });
  }


  public confirmAdd(): void {
    const ugovor = this.createFromForm();
    this.subscribeToSaveResponse(this.ugovorService.create(ugovor));
    this.dialogRef.close();
  }
  save(): void {
    this.isSaving = true;
    const ugovor = this.createFromForm();
    if (ugovor.id !== undefined) {
      this.subscribeToSaveResponse(this.ugovorService.update(ugovor));
    } else {
      this.subscribeToSaveResponse(this.ugovorService.create(ugovor));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUgovor>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(

    );
  }



  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ugovor: IUgovor): void {
    this.editForm.patchValue({
      id: ugovor.id,
      brojUgovora: ugovor.brojUgovora,
      datumUgovora: ugovor.datumUgovora,
      brojOdluke: ugovor.brojOdluke,
      datumOdluke: ugovor.datumOdluke,
      iznosUgovoraBezPdf: ugovor.iznosUgovoraBezPdf,
      sifraPostupka: ugovor.sifraPostupka,
      sifraPonude: ugovor.sifraPonude,
      sifraPonudjaca: ugovor.sifraPonudjaca,
    });
  }

  protected createFromForm(): IUgovor {
    return {
      ...new Ugovor(),
      id: this.editForm.get(['id'])!.value,
      brojUgovora: this.editForm.get(['brojUgovora'])!.value,
      datumUgovora: this.editForm.get(['datumUgovora'])!.value,
      brojOdluke: this.editForm.get(['brojOdluke'])!.value,
      datumOdluke: this.editForm.get(['datumOdluke'])!.value,
      iznosUgovoraBezPdf: this.editForm.get(['iznosUgovoraBezPdf'])!.value,
      sifraPostupka: this.editForm.get(['sifraPostupka'])!.value,
      sifraPonude: this.editForm.get(['sifraPonude'])!.value,
      sifraPonudjaca: this.editForm.get(['sifraPonudjaca'])!.value,
    };
  }
}
