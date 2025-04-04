import useSWR from 'swr';
import {
  END_POINTS,
  InspectorAgentInfoType as InspectorAgentInfo,
} from '@pinpoint-fe/ui/src/constants';
import { swrConfigs } from './swrConfigs';
import { convertParamsToQueryString } from '@pinpoint-fe/ui/src/utils';
import { useInspectorSearchParameters } from '../searchParameters';

const getQueryString = (queryParams: Partial<InspectorAgentInfo.Parameters>) => {
  if (queryParams.agentId && queryParams.timestamp) {
    return `?${convertParamsToQueryString(queryParams)}`;
  }
  return '';
};

export const useGetInspectorAgentInfoData = () => {
  const { dateRange, agentId } = useInspectorSearchParameters();
  const to = dateRange.to.getTime();
  const queryParams = {
    agentId,
    timestamp: to,
  };
  const queryString = getQueryString(queryParams);

  return useSWR<InspectorAgentInfo.Response>(
    queryString ? `${END_POINTS.INSPECTOR_AGENT_INFO}${queryString}` : null,
    swrConfigs,
  );
};
