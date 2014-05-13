/*
 * Copyright 2014 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.aeron.conductor;

import uk.co.real_logic.aeron.InvalidDestinationHandler;
import uk.co.real_logic.aeron.util.command.SubscriberMessageFlyweight;
import uk.co.real_logic.aeron.util.concurrent.AtomicBuffer;
import uk.co.real_logic.aeron.util.protocol.ErrorHeaderFlyweight;

/**
 * Maps the error messages that come back from the conductor protocol into different error
 * handlling interfaces
 */
public class ConductorErrorHandler
{
    private final ErrorHeaderFlyweight errorHeader;
    private final SubscriberMessageFlyweight receiverMessage;
    private final InvalidDestinationHandler invalidDestination;

    public ConductorErrorHandler(final InvalidDestinationHandler invalidDestination)
    {
        errorHeader = new ErrorHeaderFlyweight();
        receiverMessage = new SubscriberMessageFlyweight();
        this.invalidDestination = invalidDestination;
    }

    public void onErrorResponse(final AtomicBuffer buffer, final int index, final int length)
    {
        errorHeader.wrap(buffer, index);
        switch (errorHeader.errorCode())
        {
            case INVALID_DESTINATION:
                receiverMessage.wrap(buffer, errorHeader.offendingHeaderOffset());
                invalidDestination.onInvalidDestination(receiverMessage.destination());
                return;
        }
    }

}
