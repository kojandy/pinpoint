import { AgentSearchList } from '../../Agent';
import { useOpenTelemetrySearchParameters } from '@pinpoint-fe/ui/src/hooks';
import {
  convertParamsToQueryString,
  getFormattedDateRange,
  getOpenTelemetryPath,
} from '@pinpoint-fe/ui/src/utils';
import { ApplicationLinkButton } from '../../Button/ApplicationLinkButton';
import { Separator } from '../../ui';
import { useNavigate } from 'react-router-dom';

export const OpenTelemetrySidebar = () => {
  const navigate = useNavigate();
  const { application, dateRange, agentId } = useOpenTelemetrySearchParameters();
  return (
    <div className="w-auto h-full min-w-auto">
      <ApplicationLinkButton />
      <Separator />
      <AgentSearchList
        selectedAgentId={agentId}
        onClickAgent={(agent) => {
          navigate(
            `${getOpenTelemetryPath(application)}?${convertParamsToQueryString({
              ...getFormattedDateRange(dateRange),
              agentId: agentId === agent?.agentId ? '' : agent?.agentId,
            })}`,
          );
        }}
      />
    </div>
  );
};
