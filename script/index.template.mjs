// Public JS API for @babashka/fs, compiled from src/babashka/fs.cljc by squint.
// The squint-compiled names (exists_QMARK_, copy_tree, ...) are re-exported for
// direct interop; the friendly camelCase names are the documented JS surface.
// Generated from script/index.template.mjs by script/gen_index.cljs (bb gen-index).
import {
%IMPORTS%
} from './lib/babashka/fs.mjs';

// camelCase (or dashed) option keys are translated to the dashed keyword
// strings the squint-compiled functions read, e.g. {posixFilePermissions}
// and {visitFile} become {'posix-file-permissions'} / {'visit-file'}.
const kebabKey = (k) => k.replace(/[A-Z]/g, (m) => '-' + m.toLowerCase());
const isPlainObject = (x) =>
  x != null && typeof x === 'object' && Object.getPrototypeOf(x) === Object.prototype;
const kebabizeArg = (x) => {
  if (!isPlainObject(x)) return x;
  const out = {};
  for (const k of Object.keys(x)) out[kebabKey(k)] = x[k];
  return out;
};
const jsFriendly = (fn) => (...args) => fn(...args.map(kebabizeArg));

%CONSTS%

export {
%EXPORTS%
};

// with-temp-dir is a compile-time macro; this is the JS-callable equivalent.
// Creates a temp dir, runs `callback(dir)`, then deletes it (unless `opts.keep`).
export function withTempDir(callback, opts) {
  const o = kebabizeArg(opts);
  const dir = create_temp_dir(o);
  try {
    return callback(dir);
  } finally {
    if (!(o && o.keep)) {
      delete_tree(dir, { force: true });
    }
  }
}
