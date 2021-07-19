import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUgovor } from '../ugovor.model';
import { UgovorService } from '../service/ugovor.service';
import { UgovorDeleteDialogComponent } from '../delete/ugovor-delete-dialog.component';
import {UgovorUpdateComponent} from "../update/ugovor-update.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'jhi-ugovor',
  templateUrl: './ugovor.component.html',
})
export class UgovorComponent implements OnInit {
  ugovors?: IUgovor[];
  isLoading = false;

  constructor(protected ugovorService: UgovorService, protected modalService: NgbModal,protected  dialog:MatDialog) {}

  loadAll(): void {
    this.isLoading = true;

    this.ugovorService.query().subscribe(
      (res: HttpResponse<IUgovor[]>) => {
        this.isLoading = false;
        this.ugovors = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }
  addNew(): any {
    const dialogRef = this.dialog.open(UgovorUpdateComponent, {
      data: { Ugovor: {}},
    });
    dialogRef.afterClosed().subscribe(() => this.loadAll());
  }
  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IUgovor): number {
    return item.id!;
  }

  delete(ugovor: IUgovor): void {
    const modalRef = this.modalService.open(UgovorDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ugovor = ugovor;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
