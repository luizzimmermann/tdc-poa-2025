import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { BehaviorSubject, Observable, of, take, timer } from 'rxjs';
import {HttpClient} from "@angular/common/http";
import Keycloak, {KeycloakTokenParsed} from "keycloak-js";

interface Item {
  id: number;
  name: string;
  description: string;
  createDate: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [CommonModule]
})
export class HomeComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private keycloak = inject(Keycloak);

  private copyMessageSubject = new BehaviorSubject<string | null>(null);
  copyMessage$ = this.copyMessageSubject.asObservable(); // Expose as Observable

  items$: Observable<Item[]> = of([]);
  token: KeycloakTokenParsed | null;

  ngOnInit(): void {
    this.reloadItems();
    this.showToken();
  }

  copyToClipboard(text: string): void {
    navigator.clipboard
      .writeText(text)
      .then(() => {
        this.copyMessageSubject.next(`"${text}" copied to clipboard!`);
        timer(3000).subscribe(() => this.copyMessageSubject.next(null));
      })
      .catch((err) => {
        this.copyMessageSubject.next('Failed to copy text. Please try again.');
      });
  }

  showToken(): void {
    this.token = this.keycloak.tokenParsed || null;
  }

  ngOnDestroy(): void {
    this.copyMessageSubject.complete();
  }

  private reloadItems() {
    this.items$ = this.http.get<Item[]>('https://localhost/api/items');
  }

  deleteItem(id: number) {
    this.http.delete(`https://localhost/api/items/${id}`).subscribe(() => {
      this.reloadItems();
    });
  }

  addItem() {
    const random = Math.random().toString(36).substring(2, 15);
    this.http.post<Item>('https://localhost/api/items', {
      name: `New Item ${random}`,
      description: `New Item Description ${random}`,
      createDate: new Date().toISOString()
    }).subscribe(() => {
      this.reloadItems();
    });
  }
}
