import { ColumnDef } from '@tanstack/react-table';
import { RxCross2 } from 'react-icons/rx';
import { Badge } from '../../ui';
import { ErrorAnalysisGroupedErrorList } from '@pinpoint-fe/ui/src/constants';
import { addCommas, format } from '@pinpoint-fe/ui/src/utils';
import { MiniChart } from '../../common/MiniChart';
import { cn } from '@pinpoint-fe/ui/src/lib';

interface ErrorGroupedTableColumnProps {
  groupBy?: string[];
  onClickGroupBy?: (group: string) => void;
}

const headerClassName = 'flex justify-center test111';
const cellClassName = 'flex items-center px-4 justify-center';

export const errorGroupedTableColumns = ({
  groupBy,
  onClickGroupBy,
}: ErrorGroupedTableColumnProps): ColumnDef<ErrorAnalysisGroupedErrorList.ErrorData>[] => [
  {
    accessorKey: 'mostRecentErrorClass',
    header: () => {
      return (
        <div className="flex flex-wrap items-center gap-1">
          <span className="mr-3">Group by</span>
          {groupBy?.map((group, i) => {
            return (
              <Badge
                className="gap-1 border cursor-pointer border-muted-foreground/40 bg-secondary"
                onClick={() => onClickGroupBy?.(group)}
                variant="secondary"
                key={i}
              >
                {group}
                <RxCross2 />
              </Badge>
            );
          })}
        </div>
      );
    },
    size: 910,
    cell: (props) => {
      const original = props.row.original;
      const fieldName = original?.groupedFieldName;

      return (
        <>
          {fieldName?.stackTraceHash && (
            <div className="mb-1 text-xxs">{fieldName.stackTraceHash}</div>
          )}
          <div className="flex items-center mb-2 space-x-1 break-all">
            <div className="w-1 h-4 rounded-sm min-w-1 bg-status-fail" />
            <div className="text-sm font-semibold line-clamp-1">
              {fieldName?.errorClassName || original.mostRecentErrorClass}
            </div>
            <div className="text-xs line-clamp-1">{fieldName?.uriTemplate}</div>
          </div>
          <div className="h-12 p-2 overflow-hidden hljs text-muted-foreground">
            <code className="break-all line-clamp-2">
              {fieldName?.errorMessage || original.mostRecentErrorMessage}
            </code>
          </div>
        </>
      );
    },
  },
  {
    accessorKey: 'firstOccurred',
    header: 'First Occured',
    cell: (props) => {
      const timestamp = props.getValue() as number;
      return format(timestamp, 'MMM do HH:mm');
    },
    meta: {
      headerClassName,
      cellClassName,
    },
    size: 128,
  },
  {
    accessorKey: 'lastOccurred',
    header: 'Last Occured',
    cell: (props) => {
      const timestamp = props.getValue() as number;
      return format(timestamp, 'MMM do HH:mm');
    },
    meta: {
      headerClassName,
      cellClassName,
    },
    size: 128,
  },
  {
    accessorKey: 'chart',
    header: 'Volume',
    meta: {
      headerClassName,
      cellClassName,
    },
    cell: (props) => {
      const chart = props.getValue() as ErrorAnalysisGroupedErrorList.ErrorData['chart'];
      return <MiniChart chart={chart} />;
    },
    size: 200,
  },
  {
    accessorKey: 'count',
    header: 'Counts',
    cell: (props) => {
      const count = props.getValue() as number;
      return addCommas(count);
    },
    meta: {
      headerClassName,
      cellClassName: cn(cellClassName, 'justify-end'),
    },
    size: 72,
  },
];
