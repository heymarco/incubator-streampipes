/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {OneOfStaticProperty} from '../../model/OneOfStaticProperty';

@Component({
  selector: 'app-static-one-of-input',
  templateUrl: './static-one-of-input.component.html',
  styleUrls: ['./static-one-of-input.component.css']
})
export class StaticOneOfInputComponent implements OnInit {

  @Input()
  staticProperty: OneOfStaticProperty;

  @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

  selectedOption: string;

  constructor() { }

  ngOnInit() {
      if (this.noneSelected()) {
          if (this.staticProperty.options.length > 0) {
              this.staticProperty.options[0].selected = true;
              this.selectedOption = this.staticProperty.options[0].id;
          }
      } else {
          this.selectedOption = this.staticProperty.options.find(option => option.selected).id;
      }
      this.inputEmitter.emit(true);
  }

  noneSelected(): boolean {
      return this.staticProperty.options.every(o => !(o.selected));
  }

  select(id) {
      this.selectedOption = this.staticProperty.options.find(option => option.id === id).id;
      for (let option of this.staticProperty.options) {
          option.selected = false;
      }
      this.staticProperty.options.find(option => option.id === id).selected = true;
      this.inputEmitter.emit(true)
  }
}
