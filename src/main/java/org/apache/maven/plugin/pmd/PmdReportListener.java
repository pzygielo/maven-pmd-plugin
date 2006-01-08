package org.apache.maven.plugin.pmd;

/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.stat.Metric;

import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

/**
 * Handle events from PMD, converting them into Doxia events.
 *
 * @author Brett Porter
 * @version $Id: PmdReportListener.java,v 1.1.1.1 2005/02/17 07:16:22 brett Exp $
 */
public class PmdReportListener
    implements ReportListener
{
    private Sink sink;

    private String sourceDirectory;

    private String currentFilename;

    private boolean fileInitialized;

    private ResourceBundle bundle;

    private List violations = new ArrayList();

    private List metrics = new ArrayList();

    public PmdReportListener( Sink sink, String sourceDirectory, ResourceBundle bundle )
    {
        this.sink = sink;
        this.sourceDirectory = sourceDirectory;
        this.bundle = bundle;
    }

    private String getTitle()
    {
        return bundle.getString( "report.pmd.title" );
    }

    public void ruleViolationAdded( RuleViolation ruleViolation )
    {
        if ( !fileInitialized )
        {
            sink.section2();
            sink.sectionTitle2();
            sink.text( currentFilename );
            sink.sectionTitle2_();

            sink.table();
            sink.tableRow();
            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.pmd.column.violation" ) );
            sink.tableHeaderCell_();
            sink.tableHeaderCell();
            sink.text( bundle.getString( "report.pmd.column.line" ) );
            sink.tableHeaderCell_();
            sink.tableRow_();

            fileInitialized = true;
        }
        violations.add( ruleViolation );
    }

    // When dealing with multiple rulesets, the violations will get out of order
    // wrt their source line number.  We re-sort them before writing them to the report.
    private void processViolations()
    {
        Collections.sort( violations, new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                return ( (RuleViolation) o1 ).getLine() - ( (RuleViolation) o2 ).getLine();
            }
        } );

        for ( Iterator it = violations.iterator(); it.hasNext(); )
        {
            RuleViolation ruleViolation = (RuleViolation) it.next();

            sink.tableRow();
            sink.tableCell();
            sink.text( ruleViolation.getDescription() );
            sink.tableCell_();
            sink.tableCell();
            // TODO: xref link the line number
            sink.text( String.valueOf( ruleViolation.getLine() ) );
            sink.tableCell_();
            sink.tableRow_();
        }
        violations.clear();
    }

    public void metricAdded( Metric metric )
    {
        if (metric.getCount() != 0) {
            // Skip metrics which have no data
            metrics.add(metric);
        }
    }

    public void beginDocument()
    {
        sink.head();
        sink.title();
        sink.text( getTitle() );
        sink.title_();
        sink.head_();

        sink.body();

        sink.section1();
        sink.sectionTitle1();
        sink.text( getTitle() );
        sink.sectionTitle1_();

        sink.paragraph();
        sink.text( bundle.getString( "report.pmd.pmdlink" ) + " " );
        sink.link( "http://pmd.sourceforge.net/" );
        sink.text( "PMD" );
        sink.link_();
        sink.paragraph_();

        // TODO overall summary

        sink.section1_();
        sink.section1();
        sink.sectionTitle1();
        sink.text( bundle.getString( "report.pmd.files" ) );
        sink.sectionTitle1_();

        // TODO files summary
    }

    public void beginFile( File file )
    {
        currentFilename = StringUtils.substring( file.getAbsolutePath(), sourceDirectory.length() + 1 );
        currentFilename = StringUtils.replace( currentFilename, "\\", "/" );
        fileInitialized = false;
    }

    public void endFile( File file )
    {
        if ( fileInitialized )
        {
            processViolations();
            sink.table_();
            sink.section2_();
        }
    }

    private void processMetrics()
    {
        sink.section1();
        sink.sectionTitle1();
        sink.text( "Metrics" );
        sink.sectionTitle1_();

        sink.table();
        sink.tableRow();
        sink.tableHeaderCell();
        sink.text( "Name" );
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text( "Count" );
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text( "High" );
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text( "Low" );
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text( "Average" );
        sink.tableHeaderCell_();
        sink.tableRow_();

        for ( Iterator iter = metrics.iterator(); iter.hasNext(); )
        {
            Metric met = (Metric) iter.next();
            sink.tableRow();
            sink.tableCell();
            sink.text( met.getMetricName() );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "" + met.getCount() );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "" + met.getHighValue() );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "" + met.getLowValue() );
            sink.tableCell_();
            sink.tableCell();
            sink.text( "" + met.getAverage() );
            sink.tableCell_();
            sink.tableRow_();
        }
        sink.table_();
        sink.section1_();
    }

    public void endDocument()
    {
        sink.section1_();

        processMetrics();

        sink.body_();

        sink.flush();

        sink.close();
    }
}
