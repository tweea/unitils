!--START SNIPPET: jbehavestory
Scenario:  send correct email
Given send an email to  willemijn.wouters@unitils.be
Then check if the email is sent


Scenario: send correct email with correct header
Given send an email with willemijn.wouters@unitils.be with header just a testheader
Then check if the email has the correct header

!--END SNIPPET: jbehavestory


!--START SNIPPET: jbehavedatabasestory
Meta: 
@SqlScript path/to/script.sql 
@Dataset path/to/dataset.xml

Scenario: Title 
Meta: 
@Dataset path/to/scenario/dataset.xml 
@ExpectedDataset path/to/scenario/expected-dataset.xml 
Given ... 
When ... 
Then ...


!--END SNIPPET: jbehavedatabasestory

!--START SNIPPET: jbehavetestlinkstory
Scenario: Title 
Meta: 
@TestLinkId PRJ-4 
Given ... 
When ... 
Then ...

!--END SNIPPET: jbehavetestlinkstory
