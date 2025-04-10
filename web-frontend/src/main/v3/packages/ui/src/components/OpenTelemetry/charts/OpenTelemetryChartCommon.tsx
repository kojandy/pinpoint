import {
  CartesianGrid,
  XAxis,
  YAxis,
  XAxisProps,
  YAxisProps,
  CartesianGridProps,
  LegendProps,
  TooltipProps,
} from 'recharts';
import { ChartLegend, ChartLegendContent, ChartTooltip } from '../../ui/chart';
import { OpenTelemetryChartTooltipContent } from './OpenTelemetryChartTooltipContent';

export interface OpenTelemetryChartCommonProps {
  gridConfig?: CartesianGridProps;
  xAxisConfig?: XAxisProps;
  yAxisConfig?: YAxisProps;
  legendConfig?: LegendProps;
  tooltipConfig?: TooltipProps<number, string> & { showTotal?: boolean };
}

export const OpenTelemetryChartCommon = (
  { gridConfig, xAxisConfig, yAxisConfig, tooltipConfig }: OpenTelemetryChartCommonProps,
  chartContainerRef: React.RefObject<HTMLDivElement>,
) => {
  const { showTotal, ...restTooltipConfig } = tooltipConfig || {};
  const chartWidth = chartContainerRef?.current?.offsetWidth || 392;

  return (
    <>
      <CartesianGrid vertical={false} {...gridConfig} />
      <XAxis
        className="text-xxs"
        tickMargin={5}
        padding={{ left: 10, right: 10 }}
        {...xAxisConfig}
      />
      <YAxis
        className="text-xxs"
        axisLine={false}
        tickLine={false}
        tickMargin={5}
        {...yAxisConfig}
      />
      <ChartTooltip
        content={
          <OpenTelemetryChartTooltipContent
            indicator="line"
            labelFormatter={(label) => xAxisConfig?.tickFormatter?.(label, 0) || ''}
            formatter={yAxisConfig?.tickFormatter}
            showTotal={showTotal}
            chartWidth={chartWidth}
          />
        }
        {...restTooltipConfig}
      />
      <ChartLegend content={<ChartLegendContent />} />
    </>
  );
};
