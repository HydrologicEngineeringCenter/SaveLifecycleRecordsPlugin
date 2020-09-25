# SaveLifecycleRecordsPlugin
A plugin developed to help manage lifecycle dss files. People want to save time series, this will minimize the overall stored records. This project is based on HydrologicEngineeringCenter/DurationPlugin

The SaveLifecycleRecordsPlugin (SLRP) is a plugin that works in FRA computes. It runs at the end of each lifecycle, saving only the user-specified DSS paths and removing the rest. The result is saved as the new lifecycle DSS file.
Note: The tool finds a match between the A, B, and C parts of the user-specified DSS path and the lifecycle DSS condensed catalog. Therefore, it is possible to get multiple saved records for a single user-specified DSS path (if A, B, and C parts are the same, but F parts are different).

Here is what I did to setup the SaveLifecycleRecordsPlugin in the WAT. Assumes you already have a working WAT alternative and just want to add this plugin to your compute sequence.

1. Put the SaveLifecycleRecordsPlugin.jar file into your WAT installation (HEC-WAT/jar/ext folder).
2. In your main watershed directory, create a “SLRPlugin” folder.
3. Inside this new folder, create an .xml file. Rename it to “NAME.slrp”. Replace NAME with the name of your desired alternative.
4. Edit the .slrp file (Notepad++ works great). To start, the data locations only need to have a Name and Parameter. You will use WAT Model Linking Editor to fill in the rest of the data. NOTE: The Model Linking Editor seems to overwrite the .slrp if the linking is bad. If you want full read/write control over the .slrp file, avoid the Model Linking Editor altogether. In this case, label each DataLocation with Model="DSS File", and then you can add a DSSPath to each downstream location.
![image1](https://discourse.hecdev.net/uploads/default/original/1X/2f67cafe493a78a6dbae141216e5cb3fe221dc3b.png)

5. Open the WAT and navigate to your watershed. Add the SaveLifecycleRecordsPlugin to the end of your compute sequence, and select the SLRP alternative that you just created.
6. Now you can use the WAT Model Linking Editor to link the location/parameter pairs with outputs from the other models in your compute sequence. Example below:
![image2](https://discourse.hecdev.net/uploads/default/original/1X/fd5e71f70de1fc346c49936ef7676ea04888fda0.png)

7. Finished .slrp file will look like this:
![image3](https://discourse.hecdev.net/uploads/default/optimized/1X/613d397e077c33bc34bc3bdc0ab24264003b3ab8_2_1035x171.png)
8. Model is ready to run now.
