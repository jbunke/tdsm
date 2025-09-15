# ![Top Down Sprite Maker](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/logo/banner.gif)

***Top Down Sprite Maker*** (*TDSM*) is a flexible and powerful **pixel art character creation tool**. It is a desktop GUI application with executable binaries distributed for Windows, macOS, and Linux distributions.

[![Buy on itch.io](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/itch-button.png)](https://flinkerflitzer.itch.io/tdsm)

[![API](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/api-button.png)](https://github.com/jbunke/tdsm-api)
[![Changelog](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/changelog-button.png)](https://github.com/jbunke/tdsm/blob/master/res/text/changelog.txt)
[![Roadmap](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/roadmap-button.png)](https://github.com/jbunke/tdsm/blob/master/res/text/roadmap.txt)

---

## Trailer

<iframe width="560" height="315" src="https://www.youtube.com/embed/lYOh-t0OZ1Q?si=UUOihXgc2IR9mAzo" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

---

## Sprite styles

While most similar programs are built around a specific sprite style (art style, directions, supported animations, etc.), *TDSM* is built to support **multiple** sprite styles. *TDSM* sprite styles are modular, packaged in **ZIP archives**, and can thus be **created, modified, and shared** by the community.

### Flexibility

*TDSM* sprite styles define their own composition **layers**, supported **directions** (whether 4, 6, or 8), and **animations**.

### Getting and using sprite styles

The program launches with a basic "Default" 8-directional 32-bit sprite style that acts as a sort of tech demo.

You can download/buy additional sprite styles online. Sprite styles made or approved by me are added to [this Itch.io collection](https://itch.io/c/5834066/top-down-sprite-maker-approved-sprite-styles). Sprite styles inspired by established IPs, such as *Pokémon*, are always distributed for free.

### Making your own sprite styles

> As of the release of v1.2.0, the *DeltaScript* language specification is outdated and does not reflect the semantics of the language interpreter that runs in *TDSM*. This resource will be updated as soon as possible.

Making sprite styles for TDSM consists of two main components:

* Programming a `manifest.tds` script file that defines the sprite style's composition logic
* Drawing the assets that are composed according to the rules defined in the script

Scripts are written in [*DeltaScript*](https://github.com/jbunke/deltascript) (a scripting language I designed for use with specific applications) and the [*TDSM* scripting API](https://github.com/jbunke/tdsm-api).

Here is a full step-by-step tutorial:

<iframe width="560" height="315" src="https://www.youtube.com/embed/jqZTsHniSUE?si=rNxG0-Sz3Ol5Oncn" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

---

## Features

### Customize

*TDSM* gives you complete freedom to customize every facet of your character, yet the sprite assembly rules still ensure that sprites always look good and production-ready.

* **Controlled randomization**: Generate random sprites with the click of a button. Randomization can be constrained by **locking** customization layers you wish to exclude. [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/logo/itch/feat-lock-layers.gif)
* **Smart layering rules**: Customization layers update dynamically based on changes to other layers they depend on. For example, changing your body type from "average" to "small" in the [*Pokémon* Gen. 4 Trainer](https://flinkerflitzer.itch.io/pokemon-gen-4-trainer) style will render your head a pixel lower and switch to the small body clothing assets, but the outfit choice will stay the same.

### Configure

Configure your sprite sheet to your exact needs. Have *TDSM* adapt to your existing projects rather than having to rework code or sprite sheet slicing configurations.

* **Sizing**: Crop or pad individual sprites to your liking, ranging from 1x1 px to 128x128 px [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/padding.gif)
* **Sequencing**: Determine which directions and animations to include in the export, and **in which order** [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/sequencing.gif)
* **Layout**: Determine the axis along which directions and animations are exported in the sprite sheet, as well as how distinct animations follow one another [**[ screenshot ]**](https://raw.githubusercontent.com/jbunke/tdsm-art/refs/heads/master/_tdsm/assets/layout.gif)

### Export

Export sprite sheets and associated data from TDSM in seconds.

* Sprite sheet as a PNG image
* Sprite sheet metadata as JSON *\[optional\]*
* Sprite sheet with customization layers separated as [*Stipple Effect*](https://github.com/stipple-effect/stipple-effect) project (`.stip`) *\[optional\]*

### Save and load sprite data

Sprite customization data can be loaded into *TDSM* by uploading a JSON metadata file that matches a sprite style present in the current program session.

---

## Contribute

You can help me develop *TDSM* by [reporting bugs](https://github.com/jbunke/tdsm/issues/new?template=bug_report.md).

## License

*TDSM* is distributed under an [end-user license agreement](./LICENSE) (EULA).

### You may...

* Use *TDSM* for personal or commercial projects
* Clone the *TDSM* source code and privately modify it to suit your needs
* Distribute or sell *TDSM* sprite styles consisting of original work

### You may not...

* Distribute or sell copies of *TDSM* (whether modified or not)
* Use *TDSM* for NFT or crypto-related projects
* Use *TDSM* to train generative AI models

> **Note:**
> 
> Some sprite styles featured in TDSM are based on established IPs. The use of sprites in such styles in commercial projects (fan games, etc.) may infringe upon the copyright of the associated copyright holder.
