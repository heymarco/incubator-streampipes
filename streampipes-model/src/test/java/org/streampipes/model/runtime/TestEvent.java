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
package org.streampipes.model.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.streampipes.model.runtime.field.PrimitiveField;

import java.util.Map;

public class TestEvent {

  @Test
  public void testPrimitiveValue() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.simpleMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    assertEquals(Integer.valueOf(1), event.getFieldBySelector("s0::timestamp").getAsPrimitive()
            .getAsInt());
  }

  @Test
  public void testNestedValue() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.nestedMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    assertEquals(Integer.valueOf(2), event.getFieldBySelector("s0::nested::timestamp2")
            .getAsPrimitive()
            .getAsInt());
  }

  @Test
  public void testPrimitiveUpdate() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.simpleMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    PrimitiveField field = event.getFieldBySelector("s0::timestamp").getAsPrimitive();

    assertEquals(Integer.valueOf(1), field.getAsInt());

    field.setValue(2);

    event.updateFieldBySelector("s0::timestamp", field);

    assertEquals(Integer.valueOf(2), event.getFieldBySelector("s0::timestamp")
            .getAsPrimitive()
            .getAsInt());
  }

  @Test
  public void testNestedUpdate() {
    Map<String, Object> runtimeMap = RuntimeTestUtils.nestedMap();
    Event event = RuntimeTestUtils.makeSimpleEvent(runtimeMap, RuntimeTestUtils.getSourceInfo());

    PrimitiveField field = event.getFieldBySelector("s0::nested::timestamp2").getAsPrimitive();

    assertEquals(Integer.valueOf(2), field.getAsInt());

    field.setValue(3);

    event.updateFieldBySelector("s0::nested::timestamp2", field);

    assertEquals(Integer.valueOf(3), event.getFieldBySelector("s0::nested::timestamp2")
            .getAsPrimitive()
            .getAsInt());
  }
}