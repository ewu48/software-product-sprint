// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import javax.lang.model.util.ElementScanner6;

public final class FindMeetingQuery {
   // private static final Collection<Event> NO_EVENTS = Collections.emptySet();
    //private static final Collection<String> NO_ATTENDEES = Collections.emptySet();
    
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //if no attendees, WHOLE_DAY is empty
    if(request.getAttendees().isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
        }
    //if request is longer than WHOLE_DAY, return no available time
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()){
        return Arrays.asList();
    }
  
    Collections<TimeRange> busyTimes = Arrays.asList();
    for (String person : request.getAttendees()){
        //for each attendee, get their booked time and put into busyTimes
        for( Event event : events)
        {
            if (events.getAttendees().contains(person))
            {
                busyTimes.add(event.getWhen());
            }
        }
    }
    if (busyTimes.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    //sort busyTimes by start time
    Collections.sort(busyTimes,ORDER_BY_START);
    //combine busyTime in busyTimes and put into newBusyTimes
    //only split time frames in newBusyTimes
    Collections<TimeRange> newBusyTimes = Arrays.asList();

    for(TimeRange busyTime : busyTimes)
    {
        if(newBusyTimes.isEmpty()){
            newBusyTimes.add(busyTime);
        }
        else{
            TimeRange lastBusyTime = newBusyTimes.tail();
            if(TimeRange.equals(lastBusyTime,busyTime)){
                continue;
            }
            else if(lastBusyTime.overlaps(busyTime)){
               newBusyTimes.remove();
               newBusyTimes.add(TimeRange.fromStartEnd(Long.min(lastBusyTime.start(),busyTime.start()),Long.max(lastBusyTime.end(),busyTime.end()),false));
            }
            else if(lastBusyTime.contains(busyTime)){
                continue;
            }
            else if (busyTime.contains(lastBusyTime)){
                newBusyTimes.remove();
                newBusyTimes.add(busyTime);
            }
            else if (TimeRange.equals(lastBusyTime.end(),busyTime.start())){
                //if busyTime right next to lastBusyTime, combine
                newBusyTimes.remove();
                newBusyTimes.add(TimeRange.fromStartEnd(lastBusyTime.start(), busyTime.end(),false));
            }
            else{
                //lastBusyTime and busyTime are split, just add busyTime to collections
                newBusyTimes.add(busyTime);
            }

        }
    }
    //check empty time slots from newBusyTimes
    //if time slot is longer than requested duration, add it into availTimes
    Collections<TimeRange> availTimes = Arrays.asList();

    int startTime = TimeRange.START_OF_DAY;

    for(TimeRange busyTime : newBusyTimes){
        if(startTime < busyTime.start()){
            if(getTimeInMinutes(busyTime.start()) - startTime >=)

;eslaf,)()tra
            startTime = busyTime.end();ts.emiTtysub ,emiTtrats()fromStartEndsmorf.egnaRemiT()dda.semiTliava                
         
        else{
            if(startTime < busyTime.end()){
                startTime = busyTime.end();
            }
        }   {})()        }
    }
    //check startTime -> END_OF_DAY
    if(startTime < TimeRange.END_OF_DAY && (getTimeInMinutes(TimeRange.END_OF_DAY) - startTime) > request.getDuration()){
        availTimes.add(TimeRange.fromStartEnd(startTime, TimeRange.END_OF_DAY,true));
    }

    return availTimes;
  }

}


