import { Component, Inject, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPonude, Ponude } from '../ponude.model';
import { PonudeService } from '../service/ponude.service';
import { IPonudjaci } from 'app/entities/ponudjaci/ponudjaci.model';
import { PonudjaciService } from 'app/entities/ponudjaci/service/ponudjaci.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PonudjaciUpdateComponent } from '../../ponudjaci/update/ponudjaci-update.component';

@Component({
  selector: 'jhi-ponude-update',
  templateUrl: './ponude-update.component.html',
})
export class PonudeUpdateComponent implements OnInit {
  isSaving = false;
  editForm: FormGroup;
  ponudjacisCollection: IPonudjaci[] = [];

  constructor(
    private router: Router,
    protected ponudeService: PonudeService,
    protected ponudjaciService: PonudjaciService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    private dialogRef: MatDialogRef<PonudeUpdateComponent>,
    @Inject(MAT_DIALOG_DATA) { id, naziv }: Ponude
  ) {
    this.editForm = this.fb.group({
      id: [id],
      naziv: [naziv],
    });
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ponude }) => {
      this.updateForm(ponude);
    });
  }

  previousState(): void {
    // window.history.back();
    this.router.navigate(['/ponude']);
  }
  public confirmAdd(): void {
    const ponude = this.createFromForm();
    this.ponudeService.create(ponude).subscribe();
    this.dialogRef.close();
  }
  save(): void {
    this.isSaving = true;
    const ponude = this.createFromForm();
    if (ponude.id !== undefined) {
      this.subscribeToSaveResponse(this.ponudeService.update(ponude));
      this.dialogRef.close();
    } else {
      this.subscribeToSaveResponse(this.ponudeService.create(ponude));
      this.dialogRef.close();
    }
  }
  close(): any {
    this.dialogRef.close();
  }
  trackPonudjaciById(index: number, item: IPonudjaci): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPonude>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ponude: IPonude): void {
    this.editForm.patchValue({
      id: ponude.id,
      naziv: ponude.naziv,
    });

    this.ponudjacisCollection = this.ponudjaciService.addPonudjaciToCollectionIfMissing(this.ponudjacisCollection, ponude.ponudjaci);
  }

  protected createFromForm(): IPonude {
    return {
      ...new Ponude(),
      id: this.editForm.get(['id'])!.value,
      naziv: this.editForm.get(['naziv'])!.value,
    };
  }
}
