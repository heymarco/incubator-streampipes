/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.runtime.field;

import java.util.Map;

public class CompositeField extends AbstractField<Map<String, AbstractField>> {

  public CompositeField(String fieldNameIn, String fieldNameOut, Map<String, AbstractField> value) {
    super(fieldNameIn, fieldNameOut, value);
  }

  public AbstractField getFieldByRuntimeName(String runtimeName) {
    return value.get(runtimeName);
  }
}
