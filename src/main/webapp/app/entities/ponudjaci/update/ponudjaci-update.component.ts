import { Component, Inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPonudjaci, Ponudjaci } from '../ponudjaci.model';
import { PonudjaciService } from '../service/ponudjaci.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'jhi-ponudjaci-update',
  templateUrl: './ponudjaci-update.component.html',
})
export class PonudjaciUpdateComponent {
  nazivPonudjaca: string | undefined;
  form: FormGroup;
  constructor(
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    private dialogRef: MatDialogRef<PonudjaciUpdateComponent>,
    @Inject(MAT_DIALOG_DATA) { id, nazivPonudjaca, datum }: Ponudjaci
  ) {
    this.nazivPonudjaca = nazivPonudjaca;

    this.form = this.fb.group({
      id: [id, Validators.required],
      nazivPonudjaca: [nazivPonudjaca, Validators.required],
      datum: [datum, Validators.required],
    });
  }

  close(): any {
    this.dialogRef.close();
  }
}
