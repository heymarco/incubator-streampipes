import {BaseNgxLineConfig} from "../base/base-ngx-line-config";

export class LineConfig extends BaseNgxLineConfig {

    getWidgetLabel(): string {
        return "line";
    }

    getWidgetName(): string {
        return "line";
    }
}