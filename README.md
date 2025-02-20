# ![Top Down Sprite Maker](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/logo/banner.gif)

***Top Down Sprite Maker*** is a flexible and powerful **pixel art character creation tool**. It is a desktop GUI application with executable binaries distributed for Windows. The JAR distribution can also be run on macOS and Linux distros with compatible versions of the Java Runtime Environment (17+).

<!-- TODO - link buttons: buy on Itch, art repo, changelog, roadmap -->

## Key Features

* **Complex customization logic:** Customization layers are connected with rules that cause them to affect each other
  * **Layer masks:** Equipping a hat or helmet will mask the hair pixels that should be confined by the headwear [**[ screenshot ]**](TODO)
  * **Dynamic updates:** Changing your body type propagates changes to your clothing layers to reflect the updated sprite dimensions without altering the data of the clothing equipped [**[ screenshot ]**](TODO)
* **Controlled randomization:** Each customization layer can be "locked"; locking a layer will ignore it when a random sprite is generated, giving the user granular control over which components may be randomized [**[ screenshot ]**](TODO)
* **100% configurable sprite sheets**
  * **Custom sprite size:** Pad or crop each animation frame at any edge for sprites ranging from 1x1 pixel to 128x128 [**[ screenshot ]**](TODO)
  * **Sequencing & Inclusion:** Determine the *order of directions and animations in the exported sprite sheet*, as well as which directions and animations to *include* [**[ screenshot ]**](TODO)
  * **Layout:** Determine the axis along which directions and animations are exported in the sprite sheet, as well as how distinct animations follow one another [**[ screenshot ]**](TODO)
* **Export formats:** In addition to the standard PNG sprite sheet, *TDSM* optionally exports the sprite sheet's metadata in a JSON file, and the sprite sheet as a [*Stipple Effect*]() project with the contents of each customization layer on its own layer in the file [**[ screenshot ]**](TODO)
* **MULTIPLE SPRITE TEMPLATES:** *TDSM* is not designed specifically for one sprite template. While it only features the *Pokémon Trainer \[Gen 4\]* sprite template at launch, it will soon be updated to feature more styles of sprites with entirely distinct animations and base assets.

## Contributing

<!-- TODO -->

## License

*TDSM* is distributed under an [end-user license agreement](TODO) (EULA).

## Dependencies

* **[_Delta Time_](TODO):** Handles GUI, execution loop, underlying menu logic, and sprite assembly
* **[_STIP Parser_](TODO):** Writes the layer-wise separated sprite sheet to a *Stipple Effect* project file (`.stip`)