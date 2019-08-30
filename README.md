# DurationPlugin
A plugin developed to evaluate duration maximums. This project was forked from Henrygeorgist/TrinityPlugin


Here is an example of the file contents for a study with two input locations and 9 output locations each:

```
<?xml version="1.0" encoding="UTF-8"?>
<DurationAlternative Name="test" Desc="">
  <DataLocations>
    <DataLocation Name="Folsom-Pool" Class="hec2.model.DataLocation" ComputeType="Computed" Parameter="ELEV" PrevModelIndex="0">
      <ModelAlternative Name="test" />
    </DataLocation>
    <DataLocation Name="Folsom-Pool" Class="hec2.model.DataLocation" ComputeType="Computed" Parameter="FLOW-IN" PrevModelIndex="0">
      <ModelAlternative Name="test" />
    </DataLocation>
  </DataLocations>
  <OutputVariables>
    <DataLocation Name="Folsom-Pool" Parameter="ELEV">
      <DurationOutputVariable Duration="3" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="6" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="12" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="24" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="36" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="48" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="72" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="96" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
	  <DurationOutputVariable Duration="120" DurationValueRepresentsDays="false" ComputeType="MinValueInMaxWindow"/>
    </DataLocation>
    <DataLocation Name="Folsom-Pool" Parameter="FLOW-IN">
	  <DurationOutputVariable Duration="3" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="6" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="12" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="24" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="36" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="48" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="72" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="96" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	  <DurationOutputVariable Duration="120" DurationValueRepresentsDays="false" ComputeType="DurationMax"/>
	</DataLocation>
  </OutputVariables>
</DurationAlternative>
```