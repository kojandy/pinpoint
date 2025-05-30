/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.applicationmap;

import com.navercorp.pinpoint.common.timeseries.time.Range;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import com.navercorp.pinpoint.web.applicationmap.link.Link;
import com.navercorp.pinpoint.web.applicationmap.link.LinkDirection;
import com.navercorp.pinpoint.web.applicationmap.link.LinkList;
import com.navercorp.pinpoint.web.applicationmap.nodes.Node;
import com.navercorp.pinpoint.web.vo.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.RECORD_STATISTICS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author emeroad
 */
public class LinkListTest {
    private static final ServiceType TOMCAT = ServiceTypeFactory.of(1010, "TOMCAT", RECORD_STATISTICS);
    private static final ServiceType BLOC = ServiceTypeFactory.of(1011, "BLOC", RECORD_STATISTICS);


    @Test
    public void testGetLinkList() {
        LinkList linkList = LinkList.of();
        assertThat(linkList.getLinkList()).isEmpty();
    }

    @Test
    public void addLinkList() {
        Link tomcatToTomcatLink = createTomcatToTomcatLink();
        LinkList copy = LinkList.of(tomcatToTomcatLink);

        LinkList.Builder original = LinkList.newBuilder();
        original.addLinkList(copy);
        assertThat(original.build().getLinkList()).hasSize(1);

        // don't copy in case of duplicated node
        original.addLinkList(copy);
        Assertions.assertEquals(1, original.size());
        assertThat(original.build().getLinkList()).hasSize(1);

    }

    private Link createTomcatToTomcatLink() {
        Node from = new Node(new Application("from", TOMCAT));
        Node to = new Node(new Application("to", TOMCAT));
        return new Link(LinkDirection.IN_LINK, from, to, Range.between(0, 0));
    }

    private Link createTomcatToBlocLink() {
        Node from = new Node(new Application("from", TOMCAT));
        Node to = new Node(new Application("to", BLOC));
        return new Link(LinkDirection.IN_LINK, from, to, Range.between(0, 0));
    }

    @Test
    public void testFindToLink() {
        Link tomcatToBlocLink = createTomcatToBlocLink();
        LinkList.Builder builder = LinkList.newBuilder();
        builder.addLink(tomcatToBlocLink);
        Link tomcatToTomcatLink = createTomcatToTomcatLink();
        builder.addLink(tomcatToTomcatLink);
        LinkList list = builder.build();

        // find all links requesting "to"
        Application toBloc = new Application("to", BLOC);
        List<Link> findToLink = list.findToLink(toBloc);
        assertThat(findToLink).hasSize(1);

        for (Link link : findToLink) {
            Application to = link.getTo().getApplication();
            Assertions.assertEquals(toBloc, to, toBloc + " " + to);
        }

        List<Link> unknown = list.findToLink(new Application("unknown", BLOC));
        assertThat(unknown).isEmpty();
    }

    @Test
    public void testFindFromLink() {
        Link tomcatToBlocLink = createTomcatToBlocLink();
        LinkList.Builder builder = LinkList.newBuilder();
        builder.addLink(tomcatToBlocLink);
        Link tomcatToTomcatLink = createTomcatToTomcatLink();
        builder.addLink(tomcatToTomcatLink);
        LinkList list = builder.build();

        // find all links for "from" to request
        Application tomcat = new Application("from", TOMCAT);
        List<Link> findFromLink = list.findFromLink(tomcat);
        Assertions.assertEquals(2, findFromLink.size());
        for (Link link : findFromLink) {
            Application linkFrom = link.getFrom().getApplication();
            Assertions.assertEquals(linkFrom, tomcat);
        }

        List<Link> unknown = list.findFromLink(new Application("unknown", TOMCAT));
        assertThat(unknown).isEmpty();
    }

    @Test
    public void testContains() {
        Link tomcatToBlocLink = createTomcatToBlocLink();
        LinkList.Builder builder = LinkList.newBuilder();
        Assertions.assertFalse(builder.build().contains(tomcatToBlocLink));

        builder.addLink(tomcatToBlocLink);

        Assertions.assertTrue(builder.build().contains(tomcatToBlocLink));
    }

    @Test
    public void testSize() {
        LinkList.Builder builder = LinkList.newBuilder();
        assertThat(builder.build().getLinkList()).isEmpty();

        builder.addLink(createTomcatToTomcatLink());
        LinkList list = builder.build();
        assertThat(list.getLinkList()).hasSize(1);
    }
}
