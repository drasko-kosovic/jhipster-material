import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPonudjaci, Ponudjaci } from '../ponudjaci.model';
import { PonudjaciService } from '../service/ponudjaci.service';
import { PonudjaciDeleteDialogComponent } from '../delete/ponudjaci-delete-dialog.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { PonudeUpdateComponent } from '../../ponude/update/ponude-update.component';
import { PonudjaciUpdateComponent } from '../update/ponudjaci-update.component';

@Component({
  selector: 'jhi-ponudjaci',
  templateUrl: './ponudjaci.component.html',
})
export class PonudjaciComponent implements OnInit {
  ponudjacis?: IPonudjaci[];

  constructor(private dialog: MatDialog, protected ponudjaciService: PonudjaciService) {}

  loadAll(): void {
    this.ponudjaciService.query().subscribe((res: HttpResponse<IPonudjaci[]>) => {
      this.ponudjacis = res.body ?? [];
      // eslint-disable-next-line no-console
      console.log(res.body);
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  startEdit({ id, nazivPonudjaca, datum }: IPonudjaci): any {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
      id,
      nazivPonudjaca,
      datum,
    };

    const dialogRef = this.dialog.open(PonudjaciUpdateComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(
      // eslint-disable-next-line no-console
      val => console.log('Dialog output:', val)
    );
  }
}
