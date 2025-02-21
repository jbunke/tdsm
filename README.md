# ![Top Down Sprite Maker](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/logo/banner.gif)

***Top Down Sprite Maker*** is a flexible and powerful **pixel art character creation tool**. It is a desktop GUI application with executable binaries distributed for Windows. The JAR distribution can also be run on macOS and Linux distros with compatible versions of the Java Runtime Environment (17+).

[![Buy on itch.io](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/itch-button.png)](https://flinkerflitzer.itch.io/tdsm)
[![Art repository](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/art-repo-button.png)](https://github.com/jbunke/tdsm-art)
[![Changelog](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/changelog-button.png)](https://github.com/jbunke/tdsm/blob/master/res/tooltips/changelog.txt)
[![Roadmap](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/roadmap-button.png)](https://github.com/jbunke/tdsm/blob/master/res/tooltips/roadmap.txt)

---

![Example](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/runthrough.gif)

<div align="center">Making myself in the Pokémon Trainer sprite style</div>

## Key Features

* **Complex customization logic:** Customization layers are connected with rules that cause them to affect each other
  * **Layer masks:** Equipping a hat or helmet will mask the hair pixels that should be confined by the headwear [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/mask.gif)
  * **Dynamic updates:** Changing your body type propagates changes to your clothing layers to reflect the updated sprite dimensions without altering the data of the clothing equipped [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/change-propagation.gif)
* **Controlled randomization:** Each customization layer can be "locked"; locking a layer will ignore it when a random sprite is generated, giving the user granular control over which components may be randomized [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/logo/itch/feat-lock-layers.gif)
* **100% configurable sprite sheets**
  * **Custom sprite size:** Pad or crop each animation frame at any edge for sprites ranging from 1x1 pixel to 128x128 [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/padding.gif)
  * **Sequencing & Inclusion:** Determine the *order of directions and animations in the exported sprite sheet*, as well as which directions and animations to *include* [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/sequencing.gif)
  * **Layout:** Determine the axis along which directions and animations are exported in the sprite sheet, as well as how distinct animations follow one another [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/layout.gif)
* **Export formats:** In addition to the standard PNG sprite sheet, *TDSM* optionally exports the sprite sheet's metadata in a JSON file, and the sprite sheet as a [*Stipple Effect*](https://github.com/stipple-effect/stipple-effect) project with the contents of each customization layer on its own layer in the file [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/export-formats.gif)

> **Note:**
> 
> *TDSM* is not designed specifically for one sprite template. While it only features the *Pokémon Trainer \[Gen 4\]* sprite template at launch, it will soon be updated to feature more styles of sprites with entirely distinct animations and base assets.

## Contributing

You can help me develop *TDSM* on both the programming and artistic fronts!

### Code

* Follow [this link](https://github.com/jbunke/tdsm/issues) and press the **New issue** button to report a bug.
* If you identify a bug that you think you can fix yourself, you are welcome to **fork the repository**, make your changes, and **submit a pull request** to the `dev` branch of this repository.

### Art

* Check out the *TDSM* [art repo](https://github.com/jbunke/tdsm-art) for information on contributing art as a pixel artist.

## License

*TDSM* is distributed under an [end-user license agreement](./LICENSE) (EULA).

## Dependencies

* **[_Delta Time_](https://github.com/jbunke/delta-time):** Handles GUI, execution loop, underlying menu logic, and sprite assembly
* **[_STIP Parser_](https://github.com/stipple-effect/stip-parser):** Writes the layer-wise separated sprite sheet to a *Stipple Effect* project file (`.stip`)
