import { APP_PATH } from '@pinpoint-fe/ui/src/constants';
import { getApplicationTypeAndName } from '@pinpoint-fe/ui/src/utils';
import { LoaderFunctionArgs, redirect } from 'react-router-dom';

export const threadDumpRouteLoader = ({ params, request }: LoaderFunctionArgs) => {
  try {
    const application = getApplicationTypeAndName(params.application!);

    if (application?.applicationName && application.serviceType) {
      const redirectPath = `${APP_PATH.SERVER_MAP}/${params.application}`;
      const queryParam = Object.fromEntries(new URL(request.url).searchParams);
      const agentId = queryParam?.agentId as string;

      if (agentId) {
        return application;
      } else {
        return redirect(redirectPath);
      }
    } else {
      return redirect(APP_PATH.SERVER_MAP);
    }
  } catch (err) {
    console.error('Error in threadDumpRouteLoader:', err);
    return null;
  }
};
